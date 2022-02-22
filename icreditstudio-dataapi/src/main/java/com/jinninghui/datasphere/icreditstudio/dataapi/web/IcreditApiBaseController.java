package com.jinninghui.datasphere.icreditstudio.dataapi.web;


import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiBaseEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiBaseService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditWorkFlowService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.ApiBaseListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.WorkFlowSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.WorkFlowResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * API表基础信息表 前端控制器
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@RestController
@RequestMapping("/apiBase")
public class IcreditApiBaseController {

    @Autowired
    private IcreditApiBaseService apiBaseService;

    @PostMapping("/list")
    public BusinessResult<BusinessPageResult> list(@RequestBody ApiBaseListRequest request) {
        return apiBaseService.getList(request);
    }
}

