package com.erp.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.erp.common.exception.BizException;
import com.erp.inventory.dto.InventoryDtos;
import com.erp.inventory.entity.*;
import com.erp.inventory.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class InventoryService {

    private static final long ORG_ID = 1L;

    private final BizInventoryStockMapper stockMapper;
    private final BizInventoryLedgerMapper ledgerMapper;
    private final BizTransferOrderHMapper transferHMapper;
    private final BizTransferOrderDMapper transferDMapper;
    private final BizCountOrderHMapper countHMapper;
    private final BizCountOrderDMapper countDMapper;

    public InventoryService(BizInventoryStockMapper stockMapper,
                            BizInventoryLedgerMapper ledgerMapper,
                            BizTransferOrderHMapper transferHMapper,
                            BizTransferOrderDMapper transferDMapper,
                            BizCountOrderHMapper countHMapper,
                            BizCountOrderDMapper countDMapper) {
        this.stockMapper = stockMapper;
        this.ledgerMapper = ledgerMapper;
        this.transferHMapper = transferHMapper;
        this.transferDMapper = transferDMapper;
        this.countHMapper = countHMapper;
        this.countDMapper = countDMapper;
    }

    public List<BizInventoryStock> queryStock(InventoryDtos.StockQuery q) {
        QueryWrapper<BizInventoryStock> qw = new QueryWrapper<BizInventoryStock>()
                .eq("org_id", ORG_ID)
                .orderByDesc("id");
        if (q != null) {
            if (q.getWarehouseId() != null) qw.eq("warehouse_id", q.getWarehouseId());
            if (q.getMaterialId() != null) qw.eq("material_id", q.getMaterialId());
            if (q.getBatchNo() != null && !q.getBatchNo().isBlank()) qw.eq("batch_no", q.getBatchNo());
            if (q.getStatus() != null && !q.getStatus().isBlank()) qw.eq("status", q.getStatus());
        }
        return stockMapper.selectList(qw);
    }

    public List<BizInventoryLedger> queryLedger(String bizType, Long bizId, Long materialId, Long warehouseId) {
        QueryWrapper<BizInventoryLedger> qw = new QueryWrapper<BizInventoryLedger>()
                .eq("org_id", ORG_ID)
                .orderByDesc("id");
        if (bizType != null && !bizType.isBlank()) qw.eq("biz_type", bizType);
        if (bizId != null) qw.eq("biz_id", bizId);
        if (materialId != null) qw.eq("material_id", materialId);
        if (warehouseId != null) qw.eq("warehouse_id", warehouseId);
        return ledgerMapper.selectList(qw);
    }

    @Transactional
    public void inbound(String bizType,
                        Long bizId,
                        Long bizLineId,
                        Long warehouseId,
                        Long locationId,
                        Long materialId,
                        String batchNo,
                        String serialNo,
                        String status,
                        BigDecimal qty,
                        String remark) {
        if (warehouseId == null || materialId == null || qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException(400, "入库参数不完整");
        }
        String st = status == null || status.isBlank() ? "AVAILABLE" : status;
        BizInventoryStock stock = findOrCreateStock(warehouseId, locationId, materialId, batchNo, serialNo, st);

        BigDecimal before = safe(stock.getQty());
        BigDecimal after = before.add(qty);
        stock.setQty(after);
        stockMapper.updateById(stock);

        insertLedger(bizType, bizId, bizLineId, warehouseId, locationId, materialId, batchNo, serialNo, st,
                before, qty, after, "IN", remark);
    }

    @Transactional
    public void outbound(String bizType,
                         Long bizId,
                         Long bizLineId,
                         Long warehouseId,
                         Long locationId,
                         Long materialId,
                         String batchNo,
                         String serialNo,
                         String status,
                         BigDecimal qty,
                         String remark) {
        if (warehouseId == null || materialId == null || qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException(400, "出库参数不完整");
        }
        String st = status == null || status.isBlank() ? "AVAILABLE" : status;
        BizInventoryStock stock = findOrCreateStock(warehouseId, locationId, materialId, batchNo, serialNo, st);

        BigDecimal before = safe(stock.getQty());
        // 允许超领：不足时继续扣减到负库存
        BigDecimal after = before.subtract(qty);
        stock.setQty(after);
        stockMapper.updateById(stock);

        insertLedger(bizType, bizId, bizLineId, warehouseId, locationId, materialId, batchNo, serialNo, st,
                before, qty.negate(), after, "OUT", remark);
    }

    public Map<String, Object> check(Long warehouseId,
                                     Long locationId,
                                     Long materialId,
                                     String batchNo,
                                     String serialNo,
                                     String status,
                                     BigDecimal requiredQty) {
        String st = status == null || status.isBlank() ? "AVAILABLE" : status;
        BizInventoryStock stock = findStock(warehouseId, locationId, materialId, batchNo, serialNo, st);
        BigDecimal qty = stock == null ? BigDecimal.ZERO : safe(stock.getQty());
        BigDecimal locked = stock == null ? BigDecimal.ZERO : safe(stock.getLockedQty());
        BigDecimal available = qty.subtract(locked);

        Map<String, Object> res = new HashMap<>();
        res.put("qty", qty);
        res.put("lockedQty", locked);
        res.put("availableQty", available);
        res.put("requiredQty", safe(requiredQty));
        res.put("enough", requiredQty == null || available.compareTo(requiredQty) >= 0);
        return res;
    }

    @Transactional
    public int rollback(String bizType, Long bizId) {
        if (bizType == null || bizType.isBlank() || bizId == null) throw new BizException(400, "回滚参数不完整");
        List<BizInventoryLedger> rows = ledgerMapper.selectList(new QueryWrapper<BizInventoryLedger>()
                .eq("org_id", ORG_ID)
                .eq("biz_type", bizType)
                .eq("biz_id", bizId)
                .orderByAsc("id"));

        int n = 0;
        for (BizInventoryLedger row : rows) {
            BigDecimal change = safe(row.getQtyChange());
            BigDecimal reverse = change.negate();

            BizInventoryStock stock = findOrCreateStock(row.getWarehouseId(), row.getLocationId(), row.getMaterialId(),
                    row.getBatchNo(), row.getSerialNo(), row.getStatus());
            BigDecimal before = safe(stock.getQty());
            BigDecimal after = before.add(reverse);
            stock.setQty(after);
            stockMapper.updateById(stock);

            insertLedger(bizType + "_ROLLBACK", bizId, row.getBizLineId(), row.getWarehouseId(), row.getLocationId(),
                    row.getMaterialId(), row.getBatchNo(), row.getSerialNo(), row.getStatus(),
                    before, reverse, after,
                    reverse.compareTo(BigDecimal.ZERO) >= 0 ? "IN" : "OUT",
                    "rollback from ledger#" + row.getId());
            n++;
        }
        return n;
    }

    public List<BizTransferOrderH> pageTransfers(String keyword) {
        List<BizTransferOrderH> all = transferHMapper.selectList(new QueryWrapper<BizTransferOrderH>()
                .eq("org_id", ORG_ID)
                .eq("del_flag", 0)
                .orderByDesc("id"));
        if (keyword != null && !keyword.isBlank()) {
            return all.stream().filter(x -> String.valueOf(x.getTransferNo()).contains(keyword)).toList();
        }
        return all;
    }

    public Map<String, Object> transferDetail(Long id) {
        BizTransferOrderH h = mustTransfer(id);
        List<BizTransferOrderD> lines = transferDMapper.selectList(new QueryWrapper<BizTransferOrderD>()
                .eq("org_id", ORG_ID)
                .eq("transfer_id", id)
                .eq("del_flag", 0)
                .orderByAsc("line_no"));
        return Map.of("header", h, "lines", lines);
    }

    @Transactional
    public Map<String, Object> saveTransfer(InventoryDtos.TransferSaveReq req) {
        BizTransferOrderH h = req.getHeader();
        List<BizTransferOrderD> lines = Optional.ofNullable(req.getLines()).orElse(List.of());
        if (h == null || lines.isEmpty()) throw new BizException(400, "调拨单头/行不能为空");

        if (h.getId() == null) {
            h.setOrgId(ORG_ID);
            h.setTransferNo(Optional.ofNullable(h.getTransferNo()).filter(s -> !s.isBlank()).orElse(nextNo("TR")));
            h.setBizDate(Optional.ofNullable(h.getBizDate()).orElse(LocalDate.now()));
            h.setStatus("DRAFT");
            h.setApprovalStatus("DRAFT");
            h.setExecutedQty(BigDecimal.ZERO);
            h.setTotalQty(sumTransferQty(lines));
            h.setDelFlag(0);
            transferHMapper.insert(h);
        } else {
            BizTransferOrderH old = mustTransfer(h.getId());
            if ("APPROVED".equals(old.getStatus())) throw new BizException(400, "已审核调拨单不可编辑");
            old.setFromWarehouseId(h.getFromWarehouseId());
            old.setToWarehouseId(h.getToWarehouseId());
            old.setBizDate(Optional.ofNullable(h.getBizDate()).orElse(old.getBizDate()));
            old.setRemark(h.getRemark());
            old.setTotalQty(sumTransferQty(lines));
            transferHMapper.updateById(old);
            h = old;
            transferDMapper.delete(new QueryWrapper<BizTransferOrderD>().eq("org_id", ORG_ID).eq("transfer_id", h.getId()));
        }

        int idx = 1;
        for (BizTransferOrderD line : lines) {
            line.setId(null);
            line.setOrgId(ORG_ID);
            line.setTransferId(h.getId());
            line.setLineNo(idx++);
            line.setExecutedQty(BigDecimal.ZERO);
            line.setStatus("OPEN");
            line.setDelFlag(0);
            if (line.getQty() == null || line.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BizException(400, "调拨数量必须大于0");
            }
            transferDMapper.insert(line);
        }
        return transferDetail(h.getId());
    }

    @Transactional
    public BizTransferOrderH auditTransfer(Long id, InventoryDtos.AuditReq req) {
        BizTransferOrderH h = mustTransfer(id);
        boolean ok = Boolean.TRUE.equals(req.getApproved());
        if (!ok) {
            h.setApprovalStatus("REJECTED");
            h.setStatus("REJECTED");
            transferHMapper.updateById(h);
            return h;
        }
        if ("APPROVED".equals(h.getStatus())) return h;

        List<BizTransferOrderD> lines = transferDMapper.selectList(new QueryWrapper<BizTransferOrderD>()
                .eq("org_id", ORG_ID)
                .eq("transfer_id", id)
                .eq("del_flag", 0)
                .orderByAsc("line_no"));

        BigDecimal total = BigDecimal.ZERO;
        for (BizTransferOrderD line : lines) {
            outbound("TRANSFER", h.getId(), line.getId(), h.getFromWarehouseId(), line.getFromLocationId(),
                    line.getMaterialId(), line.getBatchNo(), line.getSerialNo(), "AVAILABLE", line.getQty(), "调拨出");
            inbound("TRANSFER", h.getId(), line.getId(), h.getToWarehouseId(), line.getToLocationId(),
                    line.getMaterialId(), line.getBatchNo(), line.getSerialNo(), "AVAILABLE", line.getQty(), "调拨入");

            line.setExecutedQty(safe(line.getQty()));
            line.setStatus("DONE");
            transferDMapper.updateById(line);
            total = total.add(safe(line.getQty()));
        }

        h.setApprovalStatus("APPROVED");
        h.setStatus("APPROVED");
        h.setExecutedQty(total);
        transferHMapper.updateById(h);
        return h;
    }

    public List<BizCountOrderH> pageCounts(String keyword) {
        List<BizCountOrderH> all = countHMapper.selectList(new QueryWrapper<BizCountOrderH>()
                .eq("org_id", ORG_ID)
                .eq("del_flag", 0)
                .orderByDesc("id"));
        if (keyword != null && !keyword.isBlank()) {
            return all.stream().filter(x -> String.valueOf(x.getCountNo()).contains(keyword)).toList();
        }
        return all;
    }

    public Map<String, Object> countDetail(Long id) {
        BizCountOrderH h = mustCount(id);
        List<BizCountOrderD> lines = countDMapper.selectList(new QueryWrapper<BizCountOrderD>()
                .eq("org_id", ORG_ID)
                .eq("count_id", id)
                .eq("del_flag", 0)
                .orderByAsc("line_no"));
        return Map.of("header", h, "lines", lines);
    }

    @Transactional
    public Map<String, Object> saveCount(InventoryDtos.CountSaveReq req) {
        BizCountOrderH h = req.getHeader();
        List<BizCountOrderD> lines = Optional.ofNullable(req.getLines()).orElse(List.of());
        if (h == null || lines.isEmpty()) throw new BizException(400, "盘点单头/行不能为空");

        if (h.getId() == null) {
            h.setOrgId(ORG_ID);
            h.setCountNo(Optional.ofNullable(h.getCountNo()).filter(s -> !s.isBlank()).orElse(nextNo("CT")));
            h.setBizDate(Optional.ofNullable(h.getBizDate()).orElse(LocalDate.now()));
            h.setStatus("DRAFT");
            h.setApprovalStatus("DRAFT");
            h.setTotalQty(sumCountQty(lines));
            h.setDiffQty(BigDecimal.ZERO);
            h.setDelFlag(0);
            countHMapper.insert(h);
        } else {
            BizCountOrderH old = mustCount(h.getId());
            if ("APPROVED".equals(old.getStatus())) throw new BizException(400, "已审核盘点单不可编辑");
            old.setWarehouseId(h.getWarehouseId());
            old.setBizDate(Optional.ofNullable(h.getBizDate()).orElse(old.getBizDate()));
            old.setRemark(h.getRemark());
            old.setTotalQty(sumCountQty(lines));
            countHMapper.updateById(old);
            h = old;
            countDMapper.delete(new QueryWrapper<BizCountOrderD>().eq("org_id", ORG_ID).eq("count_id", h.getId()));
        }

        int idx = 1;
        for (BizCountOrderD line : lines) {
            line.setId(null);
            line.setOrgId(ORG_ID);
            line.setCountId(h.getId());
            line.setLineNo(idx++);
            line.setStatus("PENDING");
            line.setQtyBook(BigDecimal.ZERO);
            line.setDiffQty(BigDecimal.ZERO);
            line.setDelFlag(0);
            if (line.getQtyActual() == null || line.getQtyActual().compareTo(BigDecimal.ZERO) < 0) {
                throw new BizException(400, "实盘数量不能小于0");
            }
            countDMapper.insert(line);
        }
        return countDetail(h.getId());
    }

    @Transactional
    public BizCountOrderH auditCount(Long id, InventoryDtos.AuditReq req) {
        BizCountOrderH h = mustCount(id);
        boolean ok = Boolean.TRUE.equals(req.getApproved());
        if (!ok) {
            h.setApprovalStatus("REJECTED");
            h.setStatus("REJECTED");
            countHMapper.updateById(h);
            return h;
        }
        if ("APPROVED".equals(h.getStatus())) return h;

        List<BizCountOrderD> lines = countDMapper.selectList(new QueryWrapper<BizCountOrderD>()
                .eq("org_id", ORG_ID)
                .eq("count_id", id)
                .eq("del_flag", 0)
                .orderByAsc("line_no"));

        BigDecimal diffTotal = BigDecimal.ZERO;
        for (BizCountOrderD line : lines) {
            BizInventoryStock stock = findStock(h.getWarehouseId(), line.getLocationId(), line.getMaterialId(), line.getBatchNo(), line.getSerialNo(), "AVAILABLE");
            BigDecimal book = stock == null ? BigDecimal.ZERO : safe(stock.getQty());
            BigDecimal actual = safe(line.getQtyActual());
            BigDecimal diff = actual.subtract(book);

            line.setQtyBook(book);
            line.setDiffQty(diff);
            line.setStatus("DONE");
            countDMapper.updateById(line);

            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                inbound("COUNT", h.getId(), line.getId(), h.getWarehouseId(), line.getLocationId(), line.getMaterialId(),
                        line.getBatchNo(), line.getSerialNo(), "AVAILABLE", diff, "盘盈调整");
            } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
                outbound("COUNT", h.getId(), line.getId(), h.getWarehouseId(), line.getLocationId(), line.getMaterialId(),
                        line.getBatchNo(), line.getSerialNo(), "AVAILABLE", diff.abs(), "盘亏调整");
            }
            diffTotal = diffTotal.add(diff);
        }

        h.setApprovalStatus("APPROVED");
        h.setStatus("APPROVED");
        h.setDiffQty(diffTotal);
        countHMapper.updateById(h);
        return h;
    }

    private void insertLedger(String bizType,
                              Long bizId,
                              Long bizLineId,
                              Long warehouseId,
                              Long locationId,
                              Long materialId,
                              String batchNo,
                              String serialNo,
                              String status,
                              BigDecimal before,
                              BigDecimal change,
                              BigDecimal after,
                              String direction,
                              String remark) {
        BizInventoryLedger ledger = new BizInventoryLedger();
        ledger.setOrgId(ORG_ID);
        ledger.setBizType(Optional.ofNullable(bizType).orElse("MANUAL"));
        ledger.setBizId(Optional.ofNullable(bizId).orElse(0L));
        ledger.setBizLineId(bizLineId);
        ledger.setWarehouseId(warehouseId);
        ledger.setLocationId(locationId);
        ledger.setMaterialId(materialId);
        ledger.setBatchNo(batchNo);
        ledger.setSerialNo(serialNo);
        ledger.setStatus(status);
        ledger.setQtyBefore(before);
        ledger.setQtyChange(change);
        ledger.setQtyAfter(after);
        ledger.setDirection(direction);
        ledger.setRemark(remark);
        ledgerMapper.insert(ledger);
    }

    private BizInventoryStock findStock(Long warehouseId,
                                        Long locationId,
                                        Long materialId,
                                        String batchNo,
                                        String serialNo,
                                        String status) {
        QueryWrapper<BizInventoryStock> qw = new QueryWrapper<BizInventoryStock>()
                .eq("org_id", ORG_ID)
                .eq("warehouse_id", warehouseId)
                .eq("material_id", materialId)
                .eq("status", status)
                .last("LIMIT 1");

        if (locationId == null) qw.isNull("location_id"); else qw.eq("location_id", locationId);
        if (batchNo == null || batchNo.isBlank()) qw.isNull("batch_no"); else qw.eq("batch_no", batchNo);
        if (serialNo == null || serialNo.isBlank()) qw.isNull("serial_no"); else qw.eq("serial_no", serialNo);
        return stockMapper.selectOne(qw);
    }

    private BizInventoryStock findOrCreateStock(Long warehouseId,
                                                Long locationId,
                                                Long materialId,
                                                String batchNo,
                                                String serialNo,
                                                String status) {
        BizInventoryStock stock = findStock(warehouseId, locationId, materialId, batchNo, serialNo, status);
        if (stock != null) return stock;

        stock = new BizInventoryStock();
        stock.setOrgId(ORG_ID);
        stock.setWarehouseId(warehouseId);
        stock.setLocationId(locationId);
        stock.setMaterialId(materialId);
        stock.setBatchNo(batchNo);
        stock.setSerialNo(serialNo);
        stock.setStatus(status);
        stock.setQty(BigDecimal.ZERO);
        stock.setLockedQty(BigDecimal.ZERO);
        stockMapper.insert(stock);
        return stock;
    }

    private BizTransferOrderH mustTransfer(Long id) {
        BizTransferOrderH h = transferHMapper.selectById(id);
        if (h == null || h.getDelFlag() != 0) throw new BizException(404, "调拨单不存在");
        return h;
    }

    private BizCountOrderH mustCount(Long id) {
        BizCountOrderH h = countHMapper.selectById(id);
        if (h == null || h.getDelFlag() != 0) throw new BizException(404, "盘点单不存在");
        return h;
    }

    private BigDecimal safe(BigDecimal x) { return x == null ? BigDecimal.ZERO : x; }
    private BigDecimal sumTransferQty(List<BizTransferOrderD> lines) { return lines.stream().map(x -> safe(x.getQty())).reduce(BigDecimal.ZERO, BigDecimal::add); }
    private BigDecimal sumCountQty(List<BizCountOrderD> lines) { return lines.stream().map(x -> safe(x.getQtyActual())).reduce(BigDecimal.ZERO, BigDecimal::add); }

    private String nextNo(String prefix) {
        return prefix + LocalDate.now().toString().replace("-", "") + "-" + (System.currentTimeMillis() % 1000000);
    }
}
