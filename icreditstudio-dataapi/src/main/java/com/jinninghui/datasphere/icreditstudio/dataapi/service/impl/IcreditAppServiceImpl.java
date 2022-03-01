package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.AppAuthInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAppEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditAppMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAppService;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.CharacterUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.StringLegalUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.AppEnableRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AccessTokenRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppSaveRequest;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * <p>
 * 应用 服务实现类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Service
public class IcreditAppServiceImpl extends ServiceImpl<IcreditAppMapper, IcreditAppEntity> implements IcreditAppService {

    private static final Integer STR_RAND_LENGTH = 16;
    @Autowired
    private IcreditAppMapper appMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public BusinessResult<String> saveDef(String userId, AppSaveRequest request) {
        StringLegalUtils.checkLegalNameForApp(request.getName());
        String appFlag = CharacterUtils.getRandomString(STR_RAND_LENGTH);
        while (BooleanUtils.isTrue(appMapper.hasExistappFlag(appFlag))) {
            appFlag = CharacterUtils.getRandomString(STR_RAND_LENGTH);
        }
        IcreditAppEntity appEntity = BeanCopyUtils.copyProperties(request, new IcreditAppEntity());
        appEntity.setAppFlag(appFlag);
        save(appEntity);
        AppAuthInfo appAuthInfo = BeanCopyUtils.copyProperties(appEntity, new AppAuthInfo());
        //新增应用时候，保存应用信息至redis
        redisTemplate.opsForValue().set(appFlag, JSON.toJSONString(appAuthInfo));
        return BusinessResult.success(appEntity.getId());
    }

    @Override
    public BusinessResult<List<IcreditAppEntity>> getList(AppListRequest request) {
        List<IcreditAppEntity> list = appMapper.getList(request.getAppGroupId());
        return BusinessResult.success(list);
    }

    @Override
    public BusinessResult<Boolean> enableById(String userId, AppEnableRequest request) {
        IcreditAppEntity appEntity = getById(request.getId());
        appEntity.setIsEnable(request.getIsEnable());
        boolean update = updateById(appEntity);
        return BusinessResult.success(update);
    }

    @Override
    public Boolean hasExitByGenerateId(String generateId, String appGroupId) {
        return appMapper.hasExitByGenerateId(generateId, appGroupId);
    }
}
