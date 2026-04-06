package com.erp.sales.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.AuditEntity;

import java.math.BigDecimal;

@TableName("biz_delivery_notice_d")
public class BizDeliveryNoticeD extends AuditEntity {
    private Long id;
    private Long orgId;
    private Long noticeId;
    private Integer lineNo;
    private Long orderLineId;
    private Long materialId;
    private BigDecimal noticeQty;
    private BigDecimal outboundQty;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    @Override public Long getOrgId() { return orgId; }
    @Override public void setOrgId(Long orgId) { this.orgId = orgId; }
    public Long getNoticeId() { return noticeId; }
    public void setNoticeId(Long noticeId) { this.noticeId = noticeId; }
    public Integer getLineNo() { return lineNo; }
    public void setLineNo(Integer lineNo) { this.lineNo = lineNo; }
    public Long getOrderLineId() { return orderLineId; }
    public void setOrderLineId(Long orderLineId) { this.orderLineId = orderLineId; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public BigDecimal getNoticeQty() { return noticeQty; }
    public void setNoticeQty(BigDecimal noticeQty) { this.noticeQty = noticeQty; }
    public BigDecimal getOutboundQty() { return outboundQty; }
    public void setOutboundQty(BigDecimal outboundQty) { this.outboundQty = outboundQty; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
