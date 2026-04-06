package com.erp.base.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.AuditEntity;

@TableName("md_dict_type")
public class MdDictType extends AuditEntity {
    private Long id; private Long orgId; private String dictType; private String dictName; private String status;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    @Override public Long getOrgId() { return orgId; } @Override public void setOrgId(Long orgId) { this.orgId = orgId; }
    public String getDictType() { return dictType; } public void setDictType(String dictType) { this.dictType = dictType; }
    public String getDictName() { return dictName; } public void setDictName(String dictName) { this.dictName = dictName; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
}
