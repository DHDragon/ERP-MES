package com.erp.base.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.AuditEntity;

@TableName("md_dict_item")
public class MdDictItem extends AuditEntity {
    private Long id; private Long orgId; private Long dictTypeId; private String itemCode; private String itemName; private Integer itemSort; private String status;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    @Override public Long getOrgId() { return orgId; } @Override public void setOrgId(Long orgId) { this.orgId = orgId; }
    public Long getDictTypeId() { return dictTypeId; } public void setDictTypeId(Long dictTypeId) { this.dictTypeId = dictTypeId; }
    public String getItemCode() { return itemCode; } public void setItemCode(String itemCode) { this.itemCode = itemCode; }
    public String getItemName() { return itemName; } public void setItemName(String itemName) { this.itemName = itemName; }
    public Integer getItemSort() { return itemSort; } public void setItemSort(Integer itemSort) { this.itemSort = itemSort; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
}
