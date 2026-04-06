package com.erp.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.AuditEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("biz_purchase_order_h")
public class BizPurchaseOrderH extends AuditEntity {
    private Long id;
    private Long orgId;
    private String orderNo;
    private Long reqId;
    private Long supplierId;
    private LocalDate bizDate;
    private String status;
    private String approvalStatus;
    private BigDecimal totalQty;
    private BigDecimal receivedQty;
    private BigDecimal inboundQty;
    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    @Override public Long getOrgId() { return orgId; }
    @Override public void setOrgId(Long orgId) { this.orgId = orgId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getReqId() { return reqId; }
    public void setReqId(Long reqId) { this.reqId = reqId; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public LocalDate getBizDate() { return bizDate; }
    public void setBizDate(LocalDate bizDate) { this.bizDate = bizDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
    public BigDecimal getTotalQty() { return totalQty; }
    public void setTotalQty(BigDecimal totalQty) { this.totalQty = totalQty; }
    public BigDecimal getReceivedQty() { return receivedQty; }
    public void setReceivedQty(BigDecimal receivedQty) { this.receivedQty = receivedQty; }
    public BigDecimal getInboundQty() { return inboundQty; }
    public void setInboundQty(BigDecimal inboundQty) { this.inboundQty = inboundQty; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
