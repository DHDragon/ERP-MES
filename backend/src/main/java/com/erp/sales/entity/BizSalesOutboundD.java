package com.erp.sales.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.AuditEntity;

import java.math.BigDecimal;

@TableName("biz_sales_outbound_d")
public class BizSalesOutboundD extends AuditEntity {
    private Long id;
    private Long orgId;
    private Long outboundId;
    private Integer lineNo;
    private Long noticeLineId;
    private Long materialId;
    private BigDecimal outboundQty;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    @Override public Long getOrgId() { return orgId; }
    @Override public void setOrgId(Long orgId) { this.orgId = orgId; }
    public Long getOutboundId() { return outboundId; }
    public void setOutboundId(Long outboundId) { this.outboundId = outboundId; }
    public Integer getLineNo() { return lineNo; }
    public void setLineNo(Integer lineNo) { this.lineNo = lineNo; }
    public Long getNoticeLineId() { return noticeLineId; }
    public void setNoticeLineId(Long noticeLineId) { this.noticeLineId = noticeLineId; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public BigDecimal getOutboundQty() { return outboundQty; }
    public void setOutboundQty(BigDecimal outboundQty) { this.outboundQty = outboundQty; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
