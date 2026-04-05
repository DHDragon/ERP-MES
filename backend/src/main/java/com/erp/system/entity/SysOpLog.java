package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.AuditEntity;

@TableName("sys_op_log")
public class SysOpLog extends AuditEntity {
    private Long id;
    private Long orgId;
    private String username;
    private String uri;
    private String httpMethod;
    private String classMethod;
    private Long costMs;
    private Integer success;
    private String errorMsg;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @Override
    public Long getOrgId() { return orgId; }

    @Override
    public void setOrgId(Long orgId) { this.orgId = orgId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri = uri; }

    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }

    public String getClassMethod() { return classMethod; }
    public void setClassMethod(String classMethod) { this.classMethod = classMethod; }

    public Long getCostMs() { return costMs; }
    public void setCostMs(Long costMs) { this.costMs = costMs; }

    public Integer getSuccess() { return success; }
    public void setSuccess(Integer success) { this.success = success; }

    public String getErrorMsg() { return errorMsg; }
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }
}
