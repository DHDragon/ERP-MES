package com.erp.sales.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.AuditEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("biz_sales_outbound_h")
public class BizSalesOutboundH extends AuditEntity {
    private Long id;
    private Long orgId;
    private String outboundNo;
    private Long noticeId;
    private LocalDate bizDate;
    private String status;
    private BigDecimal totalQty;
    private String pdaRecordJson;
    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    @Override public Long getOrgId() { return orgId; }
    @Override public void setOrgId(Long orgId) { this.orgId = orgId; }
    public String getOutboundNo() { return outboundNo; }
    public void setOutboundNo(String outboundNo) { this.outboundNo = outboundNo; }
    public Long getNoticeId() { return noticeId; }
    public void setNoticeId(Long noticeId) { this.noticeId = noticeId; }
    public LocalDate getBizDate() { return bizDate; }
    public void setBizDate(LocalDate bizDate) { this.bizDate = bizDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getTotalQty() { return totalQty; }
    public void setTotalQty(BigDecimal totalQty) { this.totalQty = totalQty; }
    public String getPdaRecordJson() { return pdaRecordJson; }
    public void setPdaRecordJson(String pdaRecordJson) { this.pdaRecordJson = pdaRecordJson; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
