package com.erp.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.AuditEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("biz_purchase_inbound_h")
public class BizPurchaseInboundH extends AuditEntity {
    private Long id;
    private Long orgId;
    private String inboundNo;
    private Long receiptId;
    private LocalDate bizDate;
    private String status;
    private BigDecimal totalQty;
    private Integer qcRequired;
    private Integer qcPassed;
    private String inspectionHookStatus;
    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    @Override public Long getOrgId() { return orgId; }
    @Override public void setOrgId(Long orgId) { this.orgId = orgId; }
    public String getInboundNo() { return inboundNo; }
    public void setInboundNo(String inboundNo) { this.inboundNo = inboundNo; }
    public Long getReceiptId() { return receiptId; }
    public void setReceiptId(Long receiptId) { this.receiptId = receiptId; }
    public LocalDate getBizDate() { return bizDate; }
    public void setBizDate(LocalDate bizDate) { this.bizDate = bizDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getTotalQty() { return totalQty; }
    public void setTotalQty(BigDecimal totalQty) { this.totalQty = totalQty; }
    public Integer getQcRequired() { return qcRequired; }
    public void setQcRequired(Integer qcRequired) { this.qcRequired = qcRequired; }
    public Integer getQcPassed() { return qcPassed; }
    public void setQcPassed(Integer qcPassed) { this.qcPassed = qcPassed; }
    public String getInspectionHookStatus() { return inspectionHookStatus; }
    public void setInspectionHookStatus(String inspectionHookStatus) { this.inspectionHookStatus = inspectionHookStatus; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
