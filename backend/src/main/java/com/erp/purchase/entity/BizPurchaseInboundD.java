package com.erp.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.AuditEntity;

import java.math.BigDecimal;

@TableName("biz_purchase_inbound_d")
public class BizPurchaseInboundD extends AuditEntity {
    private Long id;
    private Long orgId;
    private Long inboundId;
    private Integer lineNo;
    private Long receiptLineId;
    private Long materialId;
    private BigDecimal inboundQty;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    @Override public Long getOrgId() { return orgId; }
    @Override public void setOrgId(Long orgId) { this.orgId = orgId; }
    public Long getInboundId() { return inboundId; }
    public void setInboundId(Long inboundId) { this.inboundId = inboundId; }
    public Integer getLineNo() { return lineNo; }
    public void setLineNo(Integer lineNo) { this.lineNo = lineNo; }
    public Long getReceiptLineId() { return receiptLineId; }
    public void setReceiptLineId(Long receiptLineId) { this.receiptLineId = receiptLineId; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public BigDecimal getInboundQty() { return inboundQty; }
    public void setInboundQty(BigDecimal inboundQty) { this.inboundQty = inboundQty; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
