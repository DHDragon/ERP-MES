package com.erp.base.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.AuditEntity;

@TableName("rel_user_role")
public class RelUserRole extends AuditEntity {
    private Long id; private Long orgId; private Long userId; private Long roleId;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    @Override public Long getOrgId() { return orgId; } @Override public void setOrgId(Long orgId) { this.orgId = orgId; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public Long getRoleId() { return roleId; } public void setRoleId(Long roleId) { this.roleId = roleId; }
}
