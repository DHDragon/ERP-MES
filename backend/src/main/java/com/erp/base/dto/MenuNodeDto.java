package com.erp.base.dto;

import java.util.ArrayList;
import java.util.List;

public class MenuNodeDto {
    private Long id;
    private Long parentId;
    private String menuCode;
    private String menuName;
    private String menuType;
    private String path;
    private String component;
    private String icon;
    private String permissionCode;
    private Integer sortNo;
    private List<MenuNodeDto> children = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getMenuCode() { return menuCode; }
    public void setMenuCode(String menuCode) { this.menuCode = menuCode; }
    public String getMenuName() { return menuName; }
    public void setMenuName(String menuName) { this.menuName = menuName; }
    public String getMenuType() { return menuType; }
    public void setMenuType(String menuType) { this.menuType = menuType; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getPermissionCode() { return permissionCode; }
    public void setPermissionCode(String permissionCode) { this.permissionCode = permissionCode; }
    public Integer getSortNo() { return sortNo; }
    public void setSortNo(Integer sortNo) { this.sortNo = sortNo; }
    public List<MenuNodeDto> getChildren() { return children; }
    public void setChildren(List<MenuNodeDto> children) { this.children = children; }
}
