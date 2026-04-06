package com.erp.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.AuditEntity;

import java.math.BigDecimal;

@TableName("biz_purchase_receipt_d")
public class BizPurchaseReceiptD extends AuditEntity {
    private Long id;
    private Long orgId;
    private Long receiptId;
    private Integer lineNo;
    private Long orderLineId;
    private Long materialId;
    private BigDecimal receiptQty;
    private BigDecimal inboundQty;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    @Override public Long getOrgId() { return orgId; }
    @Override public void setOrgId(Long orgId) { this.orgId = orgId; }
    public Long getReceiptId() { return receiptId; }
    public void setReceiptId(Long receiptId) { this.receiptId = receiptId; }
    public Integer getLineNo() { return lineNo; }
    public void setLineNo(Integer lineNo) { this.lineNo = lineNo; }
    public Long getOrderLineId() { return orderLineId; }
    public void setOrderLineId(Long orderLineId) { this.orderLineId = orderLineId; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public BigDecimal getReceiptQty() { return receiptQty; }
    public void setReceiptQty(BigDecimal receiptQty) { this.receiptQty = receiptQty; }
    public BigDecimal getInboundQty() { return inboundQty; }
    public void setInboundQty(BigDecimal inboundQty) { this.inboundQty = inboundQty; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
