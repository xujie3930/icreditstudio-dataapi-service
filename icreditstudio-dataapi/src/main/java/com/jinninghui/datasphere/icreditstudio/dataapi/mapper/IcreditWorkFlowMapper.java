package com.jinninghui.datasphere.icreditstudio.dataapi.mapper;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditWorkFlowEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.WorkFlowResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.vo.WorkFlowIdAndNameVO;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * <p>
 * 业务流程 Mapper 接口
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Mapper
public interface IcreditWorkFlowMapper extends BaseMapper<IcreditWorkFlowEntity> {

    Boolean hasExit(@Param("name") String name, @Param("id") String id);

    Integer getMaxSort();

    void updateSort(@Param("sort")int size, @Param("id")String workFlowId);

    List<WorkFlowResult> searchFromName(@Param("name") String name);

    List<WorkFlowIdAndNameVO> getWorkFlowList();
}
