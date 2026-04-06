package com.erp.base.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.AuditEntity;

@TableName("rel_role_menu")
public class RelRoleMenu extends AuditEntity {
    private Long id; private Long orgId; private Long roleId; private Long menuId;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    @Override public Long getOrgId() { return orgId; } @Override public void setOrgId(Long orgId) { this.orgId = orgId; }
    public Long getRoleId() { return roleId; } public void setRoleId(Long roleId) { this.roleId = roleId; }
    public Long getMenuId() { return menuId; } public void setMenuId(Long menuId) { this.menuId = menuId; }
}
