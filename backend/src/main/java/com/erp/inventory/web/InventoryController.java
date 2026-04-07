package com.erp.inventory.web;

import com.erp.common.dto.Result;
import com.erp.inventory.dto.InventoryDtos;
import com.erp.inventory.entity.BizCountOrderH;
import com.erp.inventory.entity.BizInventoryLedger;
import com.erp.inventory.entity.BizInventoryStock;
import com.erp.inventory.entity.BizTransferOrderH;
import com.erp.inventory.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @PostMapping("/stock/query")
    public Result<List<BizInventoryStock>> stockQuery(@RequestBody(required = false) InventoryDtos.StockQuery req) {
        return Result.ok(service.queryStock(req == null ? new InventoryDtos.StockQuery() : req));
    }

    @GetMapping("/ledger")
    public Result<List<BizInventoryLedger>> ledger(@RequestParam(required = false) String bizType,
                                                   @RequestParam(required = false) Long bizId,
                                                   @RequestParam(required = false) Long materialId,
                                                   @RequestParam(required = false) Long warehouseId) {
        return Result.ok(service.queryLedger(bizType, bizId, materialId, warehouseId));
    }

    @PostMapping("/check")
    public Result<Map<String, Object>> check(@RequestBody Map<String, Object> req) {
        Long warehouseId = toLong(req.get("warehouseId"));
        Long locationId = toLong(req.get("locationId"));
        Long materialId = toLong(req.get("materialId"));
        String batchNo = req.get("batchNo") == null ? null : String.valueOf(req.get("batchNo"));
        String serialNo = req.get("serialNo") == null ? null : String.valueOf(req.get("serialNo"));
        String status = req.get("status") == null ? null : String.valueOf(req.get("status"));
        BigDecimal requiredQty = toBigDecimal(req.get("requiredQty"));
        return Result.ok(service.check(warehouseId, locationId, materialId, batchNo, serialNo, status, requiredQty));
    }

    @PostMapping("/rollback")
    public Result<Map<String, Object>> rollback(@RequestBody Map<String, Object> req) {
        String bizType = req.get("bizType") == null ? null : String.valueOf(req.get("bizType"));
        Long bizId = toLong(req.get("bizId"));
        int n = service.rollback(bizType, bizId);
        return Result.ok(Map.of("rolledBack", n));
    }

    // ---------- 调拨 ----------
    @GetMapping("/transfers")
    public Result<List<BizTransferOrderH>> transferList(@RequestParam(required = false) String keyword) {
        return Result.ok(service.pageTransfers(keyword));
    }

    @GetMapping("/transfers/{id}")
    public Result<Map<String, Object>> transferDetail(@PathVariable Long id) {
        return Result.ok(service.transferDetail(id));
    }

    @PostMapping("/transfers")
    public Result<Map<String, Object>> transferSave(@RequestBody InventoryDtos.TransferSaveReq req) {
        return Result.ok(service.saveTransfer(req));
    }

    @PostMapping("/transfers/{id}/audit")
    public Result<BizTransferOrderH> transferAudit(@PathVariable Long id, @RequestBody InventoryDtos.AuditReq req) {
        return Result.ok(service.auditTransfer(id, req));
    }

    // ---------- 盘点 ----------
    @GetMapping("/counts")
    public Result<List<BizCountOrderH>> countList(@RequestParam(required = false) String keyword) {
        return Result.ok(service.pageCounts(keyword));
    }

    @GetMapping("/counts/{id}")
    public Result<Map<String, Object>> countDetail(@PathVariable Long id) {
        return Result.ok(service.countDetail(id));
    }

    @PostMapping("/counts")
    public Result<Map<String, Object>> countSave(@RequestBody InventoryDtos.CountSaveReq req) {
        return Result.ok(service.saveCount(req));
    }

    @PostMapping("/counts/{id}/audit")
    public Result<BizCountOrderH> countAudit(@PathVariable Long id, @RequestBody InventoryDtos.AuditReq req) {
        return Result.ok(service.auditCount(id, req));
    }

    private Long toLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.longValue();
        return Long.parseLong(String.valueOf(o));
    }

    private BigDecimal toBigDecimal(Object o) {
        if (o == null) return null;
        if (o instanceof BigDecimal bd) return bd;
        if (o instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        return new BigDecimal(String.valueOf(o));
    }
}
