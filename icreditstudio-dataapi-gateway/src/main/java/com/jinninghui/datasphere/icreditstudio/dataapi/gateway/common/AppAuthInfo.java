package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common;

import com.jinninghui.datasphere.icreditstudio.framework.result.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 应用
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AppAuthInfo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String id;

    private String appFlag;

    private Integer certificationType;

    private Integer isEnable;

    private String name;

    private String secretContent;

    private String appGroupId;

    private String desc;

    //token有效期，单位小时
    private Integer period;

    private String allowIp;

    private Long periodBegin;

    private Long periodEnd;

    private Integer allowCall;

    private Long tokenUpdateTime;
}
