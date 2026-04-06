package com.erp.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.AuditEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("biz_purchase_req_h")
public class BizPurchaseReqH extends AuditEntity {
    private Long id;
    private Long orgId;
    private String reqNo;
    private String sourceType;
    private Long supplierId;
    private LocalDate bizDate;
    private String status;
    private String approvalStatus;
    private BigDecimal totalQty;
    private BigDecimal orderedQty;
    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    @Override public Long getOrgId() { return orgId; }
    @Override public void setOrgId(Long orgId) { this.orgId = orgId; }
    public String getReqNo() { return reqNo; }
    public void setReqNo(String reqNo) { this.reqNo = reqNo; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
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
    public BigDecimal getOrderedQty() { return orderedQty; }
    public void setOrderedQty(BigDecimal orderedQty) { this.orderedQty = orderedQty; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
