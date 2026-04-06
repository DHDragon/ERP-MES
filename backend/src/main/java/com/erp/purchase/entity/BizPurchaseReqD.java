package com.erp.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.AuditEntity;

import java.math.BigDecimal;

@TableName("biz_purchase_req_d")
public class BizPurchaseReqD extends AuditEntity {
    private Long id;
    private Long orgId;
    private Long reqId;
    private Integer lineNo;
    private Long materialId;
    private BigDecimal reqQty;
    private BigDecimal orderedQty;
    private String status;
    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    @Override public Long getOrgId() { return orgId; }
    @Override public void setOrgId(Long orgId) { this.orgId = orgId; }
    public Long getReqId() { return reqId; }
    public void setReqId(Long reqId) { this.reqId = reqId; }
    public Integer getLineNo() { return lineNo; }
    public void setLineNo(Integer lineNo) { this.lineNo = lineNo; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public BigDecimal getReqQty() { return reqQty; }
    public void setReqQty(BigDecimal reqQty) { this.reqQty = reqQty; }
    public BigDecimal getOrderedQty() { return orderedQty; }
    public void setOrderedQty(BigDecimal orderedQty) { this.orderedQty = orderedQty; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
