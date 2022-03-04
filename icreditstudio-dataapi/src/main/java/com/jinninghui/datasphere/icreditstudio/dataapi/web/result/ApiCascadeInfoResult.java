package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.Data;

@Data
public class ApiCascadeInfoResult {

    private String id;
    private String name;
    private ApiGroupIdAAndNameResult children;
    private Boolean leaf = false;

}
