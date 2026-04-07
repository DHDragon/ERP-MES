package com.erp.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;

@TableName("biz_count_order_d")
public class BizCountOrderD {
    private Long id;
    private Long orgId;
    private Long countId;
    private Integer lineNo;
    private Long materialId;
    private String batchNo;
    private String serialNo;
    private Long locationId;
    private BigDecimal qtyBook;
    private BigDecimal qtyActual;
    private BigDecimal diffQty;
    private String status;
    private Integer delFlag;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrgId() { return orgId; }
    public void setOrgId(Long orgId) { this.orgId = orgId; }
    public Long getCountId() { return countId; }
    public void setCountId(Long countId) { this.countId = countId; }
    public Integer getLineNo() { return lineNo; }
    public void setLineNo(Integer lineNo) { this.lineNo = lineNo; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getSerialNo() { return serialNo; }
    public void setSerialNo(String serialNo) { this.serialNo = serialNo; }
    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }
    public BigDecimal getQtyBook() { return qtyBook; }
    public void setQtyBook(BigDecimal qtyBook) { this.qtyBook = qtyBook; }
    public BigDecimal getQtyActual() { return qtyActual; }
    public void setQtyActual(BigDecimal qtyActual) { this.qtyActual = qtyActual; }
    public BigDecimal getDiffQty() { return diffQty; }
    public void setDiffQty(BigDecimal diffQty) { this.diffQty = diffQty; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getDelFlag() { return delFlag; }
    public void setDelFlag(Integer delFlag) { this.delFlag = delFlag; }
}
