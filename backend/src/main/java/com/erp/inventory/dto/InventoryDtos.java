package com.erp.inventory.dto;

import com.erp.inventory.entity.BizCountOrderD;
import com.erp.inventory.entity.BizCountOrderH;
import com.erp.inventory.entity.BizTransferOrderD;
import com.erp.inventory.entity.BizTransferOrderH;

import java.math.BigDecimal;
import java.util.List;

public class InventoryDtos {

    public static class StockQuery {
        private Long orgId;
        private Long warehouseId;
        private Long materialId;
        private String batchNo;
        private String status;

        public Long getOrgId() { return orgId; }
        public void setOrgId(Long orgId) { this.orgId = orgId; }
        public Long getWarehouseId() { return warehouseId; }
        public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
        public Long getMaterialId() { return materialId; }
        public void setMaterialId(Long materialId) { this.materialId = materialId; }
        public String getBatchNo() { return batchNo; }
        public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class StockResult {
        private Long warehouseId;
        private Long locationId;
        private Long materialId;
        private String batchNo;
        private String serialNo;
        private String status;
        private BigDecimal qty;
        private BigDecimal lockedQty;

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
        public BigDecimal getQty() { return qty; }
        public void setQty(BigDecimal qty) { this.qty = qty; }
        public BigDecimal getLockedQty() { return lockedQty; }
        public void setLockedQty(BigDecimal lockedQty) { this.lockedQty = lockedQty; }
    }

    public static class TransferSaveReq {
        private BizTransferOrderH header;
        private List<BizTransferOrderD> lines;

        public BizTransferOrderH getHeader() { return header; }
        public void setHeader(BizTransferOrderH header) { this.header = header; }
        public List<BizTransferOrderD> getLines() { return lines; }
        public void setLines(List<BizTransferOrderD> lines) { this.lines = lines; }
    }

    public static class CountSaveReq {
        private BizCountOrderH header;
        private List<BizCountOrderD> lines;

        public BizCountOrderH getHeader() { return header; }
        public void setHeader(BizCountOrderH header) { this.header = header; }
        public List<BizCountOrderD> getLines() { return lines; }
        public void setLines(List<BizCountOrderD> lines) { this.lines = lines; }
    }

    public static class AuditReq {
        private Boolean approved;
        private String remark;

        public Boolean getApproved() { return approved; }
        public void setApproved(Boolean approved) { this.approved = approved; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
    }
}
