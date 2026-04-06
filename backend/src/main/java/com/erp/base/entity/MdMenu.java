package com.erp.base.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.AuditEntity;

@TableName("md_menu")
public class MdMenu extends AuditEntity {
    private Long id; private Long orgId; private Long parentId; private String menuCode; private String menuName;
    private String menuType; private String path; private String component; private String icon; private String permissionCode;
    private Integer sortNo; private String status;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    @Override public Long getOrgId() { return orgId; } @Override public void setOrgId(Long orgId) { this.orgId = orgId; }
    public Long getParentId() { return parentId; } public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getMenuCode() { return menuCode; } public void setMenuCode(String menuCode) { this.menuCode = menuCode; }
    public String getMenuName() { return menuName; } public void setMenuName(String menuName) { this.menuName = menuName; }
    public String getMenuType() { return menuType; } public void setMenuType(String menuType) { this.menuType = menuType; }
    public String getPath() { return path; } public void setPath(String path) { this.path = path; }
    public String getComponent() { return component; } public void setComponent(String component) { this.component = component; }
    public String getIcon() { return icon; } public void setIcon(String icon) { this.icon = icon; }
    public String getPermissionCode() { return permissionCode; } public void setPermissionCode(String permissionCode) { this.permissionCode = permissionCode; }
    public Integer getSortNo() { return sortNo; } public void setSortNo(Integer sortNo) { this.sortNo = sortNo; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
}
