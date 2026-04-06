package com.erp.base.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.AuditEntity;

@TableName("md_role")
public class MdRole extends AuditEntity {
    private Long id; private Long orgId; private String roleCode; private String roleName; private String status;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    @Override public Long getOrgId() { return orgId; } @Override public void setOrgId(Long orgId) { this.orgId = orgId; }
    public String getRoleCode() { return roleCode; } public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public String getRoleName() { return roleName; } public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
}
