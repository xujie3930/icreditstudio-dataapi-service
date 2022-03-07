package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author xujie
 * @description 应用授权API,保存参数
 * @create 2022-02-25 15:08
 **/
@Data
public class AuthSaveRequest {
    //应用主键id
    @NotBlank(message = "20000021")
    private String appId;
    //API的主键id
    private List<String> apiId;
    //有效起始时间(-1表示无穷)
    private Long periodBegin;
    //有效结束时间(-1表示无穷)
    private Long periodEnd;
    //允许调用次数(-1表示无穷)
    private Integer allowCall;
}
