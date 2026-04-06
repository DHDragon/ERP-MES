package com.erp.base.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.AuditEntity;

@TableName("cfg_data_scope")
public class CfgDataScope extends AuditEntity {
    private Long id; private Long orgId; private Long roleId; private String scopeType; private String scopeOrgIds; private String status;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    @Override public Long getOrgId() { return orgId; } @Override public void setOrgId(Long orgId) { this.orgId = orgId; }
    public Long getRoleId() { return roleId; } public void setRoleId(Long roleId) { this.roleId = roleId; }
    public String getScopeType() { return scopeType; } public void setScopeType(String scopeType) { this.scopeType = scopeType; }
    public String getScopeOrgIds() { return scopeOrgIds; } public void setScopeOrgIds(String scopeOrgIds) { this.scopeOrgIds = scopeOrgIds; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
}
