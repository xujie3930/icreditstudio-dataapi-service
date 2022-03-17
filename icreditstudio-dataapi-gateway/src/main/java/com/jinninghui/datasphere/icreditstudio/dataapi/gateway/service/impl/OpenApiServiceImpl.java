package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.impl;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.ApiLogInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.CallStatusEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.DatasourceTypeEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.RedisApiInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common.DataApiGatewayPageResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common.KafkaProducer;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.interceptor.DataApiGatewayContextHolder;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.OpenApiService;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.utils.ResultSetToListUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.DBConnectionManager;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.base.BusinessBasePageForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Project：icreditstudio-dataapi-service
 * Package：com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.impl
 * ClassName: OpenApiServiceImpl
 * Description:  OpenApiServiceImpl类
 * Date: 2022/3/17 3:08 下午
 *
 * @author liyanhui
 */
@Slf4j
@Service
public class OpenApiServiceImpl implements OpenApiService {

    private static final String PAGENUM_MARK = "pageNum";
    private static final String PAGESIZE_MARK = "pageSize";
    private static final Integer PAGENUM_DEFALUT = 1;
    private static final Integer PAGESIZE_DEFALUT = 500;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Override
    public BusinessResult<Object> getData(String version, String path, Map map) {
        Connection conn = null;
        String querySql = null;
        Long dataCount = 0L;
        RedisApiInfo apiInfo = DataApiGatewayContextHolder.get().getApiInfo();
        ApiLogInfo apiLogInfo = DataApiGatewayContextHolder.get().getApiLogInfo();
        try {

            querySql = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.parseSql(apiInfo.getQuerySql(), map);
            //连接数据源，执行SQL
            conn = DBConnectionManager.getInstance().getConnectionByUserNameAndPassword(apiInfo.getUrl(), apiInfo.getUserName(), apiInfo.getPassword(), DatasourceTypeEnum.MYSQL.getType());
            if (conn == null) {
                throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000016.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000016.getMessage());
            }
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            //如果传了分页参数要加上分页 并且返回的数据要用分页对象包装:BusinessResult<BusinessPageResult> ，分页的最大条数500
            if (map.containsKey(PAGENUM_MARK) && map.containsKey(PAGESIZE_MARK)) {
                DataApiGatewayPageResult<Object> build = getPageResult(map, querySql, dataCount, apiLogInfo, stmt);
                return BusinessResult.success(build);
            } else {
                //如果不传分页最大查询500条，不需要用分页对象包装
                List list = getListResult(querySql, apiLogInfo, stmt);
                return BusinessResult.success(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //发送kafka失败信息
            ApiLogInfo failLog = generateFailLog(apiLogInfo, querySql, e);
            kafkaProducer.send(failLog);
            log.info("发送kafka异常日志:{}", failLog);
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000013.getCode(), failLog.getExceptionDetail());
        } finally {
            DBConnectionManager.getInstance().freeConnection(apiInfo.getUrl(), conn);
            DataApiGatewayContextHolder.remove();
        }
    }

    private List getListResult(String querySql, ApiLogInfo apiLogInfo, Statement stmt) throws SQLException {
        querySql = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.addPageParam(querySql, PAGENUM_DEFALUT, PAGESIZE_DEFALUT);
        log.info("查询sql:{}", querySql);
        ResultSet pagingRs = stmt.executeQuery(querySql);
        //发送成功消息
        ApiLogInfo successLog = generateSuccessLog(apiLogInfo, querySql);
        kafkaProducer.send(successLog);
        log.info("发送kafka成功日志:{}", successLog);
        if (pagingRs.next()) {
            List list = ResultSetToListUtils.convertList(pagingRs, apiLogInfo.getResponseParam());
            return list;
        }
        return null;
    }

    private DataApiGatewayPageResult<Object> getPageResult(Map map, String querySql, Long dataCount, ApiLogInfo apiLogInfo, Statement stmt) throws SQLException {
        Integer pageNum = Math.max(Integer.parseInt((String) map.get(PAGENUM_MARK)), PAGENUM_DEFALUT);
        Integer pageSize = Math.min(Integer.parseInt((String) map.get(PAGESIZE_MARK)), PAGESIZE_DEFALUT);
        String countSql = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.getSelectCountSql(querySql);
        ResultSet countRs = stmt.executeQuery(countSql);
        if (countRs.next()) {
            //rs结果集第一个参数即为记录数，且其结果集中只有一个参数
            dataCount = countRs.getLong(1);
        }
        String pageSql = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.addPageParam(querySql, pageNum, pageSize);
        log.info("查询sql分页:{}", pageSql);
        ResultSet pagingRsForPageParam = stmt.executeQuery(pageSql);
        ApiLogInfo successLog = generateSuccessLog(apiLogInfo, pageSql);
        kafkaProducer.send(successLog);
        log.info("发送kafka成功日志:{}", successLog);
        if (pagingRsForPageParam.next()) {
            List list = ResultSetToListUtils.convertList(pagingRsForPageParam, apiLogInfo.getResponseParam());
            //发送成功消息
            BusinessBasePageForm pageForm = new BusinessBasePageForm();
            pageForm.setPageNum(pageNum);
            pageForm.setPageSize(pageSize);
            DataApiGatewayPageResult build = DataApiGatewayPageResult.build(list, pageForm, dataCount);
            return build;
        }
        return null;
    }

    private ApiLogInfo generateFailLog(ApiLogInfo apiLogInfo, String querySql, Exception e) {
        apiLogInfo.setExecuteSql(querySql);
        apiLogInfo.setCallEndTime(new Date());
        apiLogInfo.setCallStatus(CallStatusEnum.CALL_FAIL.getCode());
        if (null != apiLogInfo.getCallBeginTime()) {
            apiLogInfo.setRunTime(System.currentTimeMillis() - apiLogInfo.getCallBeginTime().getTime());
        }
        if (e instanceof AppException) {
            try {
                Field errorMsg = e.getClass().getSuperclass().getDeclaredField("errorMsg");
                ReflectionUtils.makeAccessible(errorMsg);
                String errorLog = (String) errorMsg.get(e);
                apiLogInfo.setExceptionDetail(errorLog);
            } catch (Exception exception) {
                exception.printStackTrace();
                apiLogInfo.setExceptionDetail(exception.toString());
            }
        } else {
            apiLogInfo.setExceptionDetail(e.toString());
        }
        return apiLogInfo;
    }

    private ApiLogInfo generateSuccessLog(ApiLogInfo apiLogInfo, String querySql) {
        apiLogInfo.setExecuteSql(querySql);
        apiLogInfo.setCallEndTime(new Date());
        apiLogInfo.setCallStatus(CallStatusEnum.CALL_SUCCESS.getCode());
        apiLogInfo.setRunTime(System.currentTimeMillis() - apiLogInfo.getCallBeginTime().getTime());
        return apiLogInfo;
    }
}