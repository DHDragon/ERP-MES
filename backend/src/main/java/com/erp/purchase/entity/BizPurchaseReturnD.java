package com.erp.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.AuditEntity;

import java.math.BigDecimal;

@TableName("biz_purchase_return_d")
public class BizPurchaseReturnD extends AuditEntity {
    private Long id;
    private Long orgId;
    private Long returnId;
    private Integer lineNo;
    private Long materialId;
    private BigDecimal returnQty;
    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    @Override public Long getOrgId() { return orgId; }
    @Override public void setOrgId(Long orgId) { this.orgId = orgId; }
    public Long getReturnId() { return returnId; }
    public void setReturnId(Long returnId) { this.returnId = returnId; }
    public Integer getLineNo() { return lineNo; }
    public void setLineNo(Integer lineNo) { this.lineNo = lineNo; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public BigDecimal getReturnQty() { return returnQty; }
    public void setReturnQty(BigDecimal returnQty) { this.returnQty = returnQty; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
