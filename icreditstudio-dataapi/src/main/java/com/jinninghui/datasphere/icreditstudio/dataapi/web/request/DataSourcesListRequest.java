package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

public class DataSourcesListRequest {

    /**
     * 数据源类型：1-mysql，2-oracle，3-postgresql，4-sqlServer，5-MongoDB
     */
    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}