package com.erp.base.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.AuditEntity;

@TableName("cfg_system_param")
public class CfgSystemParam extends AuditEntity {
    private Long id; private Long orgId; private String paramKey; private String paramName; private String paramValue; private String status;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    @Override public Long getOrgId() { return orgId; } @Override public void setOrgId(Long orgId) { this.orgId = orgId; }
    public String getParamKey() { return paramKey; } public void setParamKey(String paramKey) { this.paramKey = paramKey; }
    public String getParamName() { return paramName; } public void setParamName(String paramName) { this.paramName = paramName; }
    public String getParamValue() { return paramValue; } public void setParamValue(String paramValue) { this.paramValue = paramValue; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
}
