package com.erp.base.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.AuditEntity;

@TableName("md_user")
public class MdUser extends AuditEntity {
    private Long id; private Long orgId; private Long deptId; private String username; private String passwordHash;
    private String realName; private String mobile; private String email; private String status;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    @Override public Long getOrgId() { return orgId; } @Override public void setOrgId(Long orgId) { this.orgId = orgId; }
    public Long getDeptId() { return deptId; } public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getUsername() { return username; } public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; } public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRealName() { return realName; } public void setRealName(String realName) { this.realName = realName; }
    public String getMobile() { return mobile; } public void setMobile(String mobile) { this.mobile = mobile; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
}
