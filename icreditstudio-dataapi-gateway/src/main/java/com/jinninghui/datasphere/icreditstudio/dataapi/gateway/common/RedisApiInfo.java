package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common;

import lombok.Data;

@Data
public class RedisApiInfo {

    private String apiId;
    private String url;
    private String userName;
    private String password;
    private String requiredFields;
    private String querySql;
}
