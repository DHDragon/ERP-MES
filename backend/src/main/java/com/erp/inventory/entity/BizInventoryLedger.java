package com.erp.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("biz_inventory_ledger")
public class BizInventoryLedger {
    private Long id;
    private Long orgId;
    private String bizType;
    private Long bizId;
    private Long bizLineId;
    private Long warehouseId;
    private Long locationId;
    private Long materialId;
    private String batchNo;
    private String serialNo;
    private String status;
    private BigDecimal qtyBefore;
    private BigDecimal qtyChange;
    private BigDecimal qtyAfter;
    private String direction;
    private String remark;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrgId() { return orgId; }
    public void setOrgId(Long orgId) { this.orgId = orgId; }
    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }
    public Long getBizId() { return bizId; }
    public void setBizId(Long bizId) { this.bizId = bizId; }
    public Long getBizLineId() { return bizLineId; }
    public void setBizLineId(Long bizLineId) { this.bizLineId = bizLineId; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getSerialNo() { return serialNo; }
    public void setSerialNo(String serialNo) { this.serialNo = serialNo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getQtyBefore() { return qtyBefore; }
    public void setQtyBefore(BigDecimal qtyBefore) { this.qtyBefore = qtyBefore; }
    public BigDecimal getQtyChange() { return qtyChange; }
    public void setQtyChange(BigDecimal qtyChange) { this.qtyChange = qtyChange; }
    public BigDecimal getQtyAfter() { return qtyAfter; }
    public void setQtyAfter(BigDecimal qtyAfter) { this.qtyAfter = qtyAfter; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
