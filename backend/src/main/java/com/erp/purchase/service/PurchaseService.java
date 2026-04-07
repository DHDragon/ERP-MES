package com.erp.purchase.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.erp.common.dto.PageResult;
import com.erp.common.exception.BizException;
import com.erp.purchase.dto.PurchaseDtos;
import com.erp.purchase.entity.*;
import com.erp.purchase.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    private static final long ORG_ID = 1L;

    private final BizPurchaseReqHMapper reqHMapper;
    private final BizPurchaseReqDMapper reqDMapper;
    private final BizPurchaseOrderHMapper orderHMapper;
    private final BizPurchaseOrderDMapper orderDMapper;
    private final BizPurchaseReceiptHMapper receiptHMapper;
    private final BizPurchaseReceiptDMapper receiptDMapper;
    private final BizPurchaseInboundHMapper inboundHMapper;
    private final BizPurchaseInboundDMapper inboundDMapper;
    private final BizPurchaseReturnHMapper returnHMapper;
    private final BizPurchaseReturnDMapper returnDMapper;
    private final PurchaseNoGenerator noGenerator;
    private final QualityInspectionStubService qualityInspectionStubService;
    private final InventoryInboundStubService inventoryInboundStubService;

    public PurchaseService(BizPurchaseReqHMapper reqHMapper,
                           BizPurchaseReqDMapper reqDMapper,
                           BizPurchaseOrderHMapper orderHMapper,
                           BizPurchaseOrderDMapper orderDMapper,
                           BizPurchaseReceiptHMapper receiptHMapper,
                           BizPurchaseReceiptDMapper receiptDMapper,
                           BizPurchaseInboundHMapper inboundHMapper,
                           BizPurchaseInboundDMapper inboundDMapper,
                           BizPurchaseReturnHMapper returnHMapper,
                           BizPurchaseReturnDMapper returnDMapper,
                           PurchaseNoGenerator noGenerator,
                           QualityInspectionStubService qualityInspectionStubService,
                           InventoryInboundStubService inventoryInboundStubService) {
        this.reqHMapper = reqHMapper;
        this.reqDMapper = reqDMapper;
        this.orderHMapper = orderHMapper;
        this.orderDMapper = orderDMapper;
        this.receiptHMapper = receiptHMapper;
        this.receiptDMapper = receiptDMapper;
        this.inboundHMapper = inboundHMapper;
        this.inboundDMapper = inboundDMapper;
        this.returnHMapper = returnHMapper;
        this.returnDMapper = returnDMapper;
        this.noGenerator = noGenerator;
        this.qualityInspectionStubService = qualityInspectionStubService;
        this.inventoryInboundStubService = inventoryInboundStubService;
    }

    // ---------- 采购申请 ----------
    public PageResult<BizPurchaseReqH> pageReqs(int pageNo, int pageSize, String keyword) {
        List<BizPurchaseReqH> all = reqHMapper.selectList(new QueryWrapper<BizPurchaseReqH>()
                .eq("org_id", ORG_ID).eq("del_flag", 0).orderByDesc("id"));
        if (keyword != null && !keyword.isBlank()) {
            all = all.stream().filter(x -> (x.getReqNo() + "").contains(keyword)).toList();
        }
        return page(all, pageNo, pageSize);
    }

    public Map<String, Object> reqDetail(Long id) {
        BizPurchaseReqH h = mustReq(id);
        List<BizPurchaseReqD> lines = reqDMapper.selectList(new QueryWrapper<BizPurchaseReqD>()
                .eq("org_id", ORG_ID).eq("req_id", id).eq("del_flag", 0).orderByAsc("line_no"));
        return Map.of("header", h, "lines", lines);
    }

    @Transactional
    public Map<String, Object> saveReq(PurchaseDtos.PurchaseReqSaveReq req) {
        BizPurchaseReqH h = req.getHeader();
        List<BizPurchaseReqD> lines = Optional.ofNullable(req.getLines()).orElse(List.of());
        if (h == null || lines.isEmpty()) throw new BizException(400, "采购申请头/行不能为空");

        if (h.getId() == null) {
            h.setOrgId(ORG_ID);
            h.setReqNo(Optional.ofNullable(h.getReqNo()).filter(s -> !s.isBlank()).orElse(noGenerator.next("PR")));
            h.setSourceType(Optional.ofNullable(h.getSourceType()).filter(s -> !s.isBlank()).orElse("MANUAL")); // 预留 MRP
            h.setBizDate(Optional.ofNullable(h.getBizDate()).orElse(LocalDate.now()));
            h.setStatus("DRAFT");
            h.setApprovalStatus("DRAFT");
            h.setOrderedQty(BigDecimal.ZERO);
            h.setTotalQty(sumReqQty(lines));
            h.setDelFlag(0);
            reqHMapper.insert(h);
        } else {
            BizPurchaseReqH old = mustReq(h.getId());
            if (Set.of("CLOSED", "VOID").contains(old.getStatus())) throw new BizException(400, "已关闭/作废采购申请不可编辑");
            old.setSupplierId(h.getSupplierId());
            old.setSourceType(Optional.ofNullable(h.getSourceType()).filter(s -> !s.isBlank()).orElse(old.getSourceType()));
            old.setBizDate(Optional.ofNullable(h.getBizDate()).orElse(old.getBizDate()));
            old.setRemark(h.getRemark());
            old.setTotalQty(sumReqQty(lines));
            reqHMapper.updateById(old);
            h = old;
            reqDMapper.delete(new QueryWrapper<BizPurchaseReqD>().eq("org_id", ORG_ID).eq("req_id", h.getId()));
        }

        int idx = 1;
        for (BizPurchaseReqD line : lines) {
            line.setId(null);
            line.setOrgId(ORG_ID);
            line.setReqId(h.getId());
            line.setLineNo(idx++);
            line.setOrderedQty(BigDecimal.ZERO);
            line.setStatus("OPEN");
            line.setDelFlag(0);
            if (line.getReqQty() == null || line.getReqQty().compareTo(BigDecimal.ZERO) <= 0) throw new BizException(400, "申请数量必须大于0");
            reqDMapper.insert(line);
        }

        return reqDetail(h.getId());
    }

    public BizPurchaseReqH submitReq(Long id) {
        BizPurchaseReqH h = mustReq(id);
        h.setApprovalStatus("PENDING");
        h.setStatus("SUBMITTED");
        reqHMapper.updateById(h);
        return h;
    }

    public BizPurchaseReqH auditReq(Long id, PurchaseDtos.AuditReq req) {
        BizPurchaseReqH h = mustReq(id);
        boolean ok = Boolean.TRUE.equals(req.getApproved());
        h.setApprovalStatus(ok ? "APPROVED" : "REJECTED");
        h.setStatus(ok ? "APPROVED" : "REJECTED");
        reqHMapper.updateById(h);
        return h;
    }

    @Transactional
    public Map<String, Object> pushToOrder(PurchaseDtos.PushOrderReq req) {
        BizPurchaseReqH source = mustReq(req.getReqId());
        if (!"APPROVED".equals(source.getApprovalStatus())) throw new BizException(400, "采购申请未审核，不得下推采购订单");

        List<BizPurchaseReqD> reqLines = reqDMapper.selectList(new QueryWrapper<BizPurchaseReqD>()
                .eq("org_id", ORG_ID).eq("req_id", source.getId()).eq("del_flag", 0));
        Map<Long, BizPurchaseReqD> reqLineMap = reqLines.stream().collect(Collectors.toMap(BizPurchaseReqD::getId, x -> x));

        List<PurchaseDtos.PushLine> pushLines = Optional.ofNullable(req.getLines()).orElse(List.of());
        if (pushLines.isEmpty()) throw new BizException(400, "下推行不能为空");

        BizPurchaseOrderH oh = new BizPurchaseOrderH();
        oh.setOrgId(ORG_ID);
        oh.setOrderNo(noGenerator.next("PO"));
        oh.setReqId(source.getId());
        oh.setSupplierId(source.getSupplierId());
        oh.setBizDate(Optional.ofNullable(req.getBizDate()).orElse(LocalDate.now()));
        oh.setStatus("DRAFT");
        oh.setApprovalStatus("DRAFT");
        oh.setReceivedQty(BigDecimal.ZERO);
        oh.setInboundQty(BigDecimal.ZERO);
        oh.setDelFlag(0);
        orderHMapper.insert(oh);

        BigDecimal total = BigDecimal.ZERO;
        int idx = 1;
        for (PurchaseDtos.PushLine pl : pushLines) {
            BizPurchaseReqD rl = reqLineMap.get(pl.getSourceLineId());
            if (rl == null) throw new BizException(400, "申请行不存在: " + pl.getSourceLineId());
            BigDecimal remain = safe(rl.getReqQty()).subtract(safe(rl.getOrderedQty()));
            if (pl.getQty() == null || pl.getQty().compareTo(BigDecimal.ZERO) <= 0) throw new BizException(400, "下推数量必须大于0");
            if (pl.getQty().compareTo(remain) > 0) throw new BizException(400, "下推数量不得超过申请未下单数量");

            BizPurchaseOrderD ol = new BizPurchaseOrderD();
            ol.setOrgId(ORG_ID);
            ol.setOrderId(oh.getId());
            ol.setLineNo(idx++);
            ol.setReqLineId(rl.getId());
            ol.setMaterialId(rl.getMaterialId());
            ol.setOrderQty(pl.getQty());
            ol.setReceivedQty(BigDecimal.ZERO);
            ol.setInboundQty(BigDecimal.ZERO);
            ol.setStatus("OPEN");
            ol.setDelFlag(0);
            orderDMapper.insert(ol);

            // 回写申请行
            rl.setOrderedQty(safe(rl.getOrderedQty()).add(pl.getQty()));
            rl.setStatus(rl.getOrderedQty().compareTo(safe(rl.getReqQty())) >= 0 ? "DONE" : "PARTIAL");
            reqDMapper.updateById(rl);
            total = total.add(pl.getQty());
        }
        oh.setTotalQty(total);
        orderHMapper.updateById(oh);

        // 回写申请头
        List<BizPurchaseReqD> latestReqLines = reqDMapper.selectList(new QueryWrapper<BizPurchaseReqD>()
                .eq("org_id", ORG_ID).eq("req_id", source.getId()).eq("del_flag", 0));
        BigDecimal orderedTotal = latestReqLines.stream().map(x -> safe(x.getOrderedQty())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal reqTotal = latestReqLines.stream().map(x -> safe(x.getReqQty())).reduce(BigDecimal.ZERO, BigDecimal::add);
        source.setOrderedQty(orderedTotal);
        source.setStatus(orderedTotal.compareTo(reqTotal) >= 0 ? "DONE" : "PARTIAL");
        reqHMapper.updateById(source);

        return orderDetail(oh.getId());
    }

    // ---------- 采购订单 ----------
    public PageResult<BizPurchaseOrderH> pageOrders(int pageNo, int pageSize, String keyword) {
        List<BizPurchaseOrderH> all = orderHMapper.selectList(new QueryWrapper<BizPurchaseOrderH>()
                .eq("org_id", ORG_ID).eq("del_flag", 0).orderByDesc("id"));
        if (keyword != null && !keyword.isBlank()) all = all.stream().filter(x -> (x.getOrderNo() + "").contains(keyword)).toList();
        return page(all, pageNo, pageSize);
    }

    public Map<String, Object> orderDetail(Long id) {
        BizPurchaseOrderH h = mustOrder(id);
        List<BizPurchaseOrderD> lines = orderDMapper.selectList(new QueryWrapper<BizPurchaseOrderD>()
                .eq("org_id", ORG_ID).eq("order_id", id).eq("del_flag", 0).orderByAsc("line_no"));
        return Map.of("header", h, "lines", lines);
    }

    @Transactional
    public Map<String, Object> saveOrder(PurchaseDtos.PurchaseOrderSaveReq req) {
        BizPurchaseOrderH h = req.getHeader();
        List<BizPurchaseOrderD> lines = Optional.ofNullable(req.getLines()).orElse(List.of());
        if (h == null || lines.isEmpty()) throw new BizException(400, "采购订单头/行不能为空");

        if (h.getId() == null) {
            h.setOrgId(ORG_ID);
            h.setOrderNo(Optional.ofNullable(h.getOrderNo()).filter(s -> !s.isBlank()).orElse(noGenerator.next("PO")));
            h.setBizDate(Optional.ofNullable(h.getBizDate()).orElse(LocalDate.now()));
            h.setStatus("DRAFT");
            h.setApprovalStatus("DRAFT");
            h.setReceivedQty(BigDecimal.ZERO);
            h.setInboundQty(BigDecimal.ZERO);
            h.setTotalQty(sumOrderQty(lines));
            h.setDelFlag(0);
            orderHMapper.insert(h);
        } else {
            BizPurchaseOrderH old = mustOrder(h.getId());
            if ("CLOSED".equals(old.getStatus())) throw new BizException(400, "已关闭采购订单不可编辑");
            old.setSupplierId(h.getSupplierId());
            old.setBizDate(Optional.ofNullable(h.getBizDate()).orElse(old.getBizDate()));
            old.setRemark(h.getRemark());
            old.setTotalQty(sumOrderQty(lines));
            orderHMapper.updateById(old);
            h = old;
            orderDMapper.delete(new QueryWrapper<BizPurchaseOrderD>().eq("org_id", ORG_ID).eq("order_id", h.getId()));
        }

        int idx = 1;
        for (BizPurchaseOrderD line : lines) {
            line.setId(null);
            line.setOrgId(ORG_ID);
            line.setOrderId(h.getId());
            line.setLineNo(idx++);
            line.setReceivedQty(BigDecimal.ZERO);
            line.setInboundQty(BigDecimal.ZERO);
            line.setStatus("OPEN");
            line.setDelFlag(0);
            if (line.getOrderQty() == null || line.getOrderQty().compareTo(BigDecimal.ZERO) <= 0) throw new BizException(400, "订单数量必须大于0");
            orderDMapper.insert(line);
        }

        return orderDetail(h.getId());
    }

    public BizPurchaseOrderH auditOrder(Long id, PurchaseDtos.AuditReq req) {
        BizPurchaseOrderH h = mustOrder(id);
        boolean ok = Boolean.TRUE.equals(req.getApproved());
        h.setApprovalStatus(ok ? "APPROVED" : "REJECTED");
        h.setStatus(ok ? "APPROVED" : "REJECTED");
        orderHMapper.updateById(h);
        return h;
    }

    public BizPurchaseOrderH closeOrder(Long id) {
        BizPurchaseOrderH h = mustOrder(id);
        h.setStatus("CLOSED");
        orderHMapper.updateById(h);
        return h;
    }

    // ---------- 收货 ----------
    public PageResult<BizPurchaseReceiptH> pageReceipts(int pageNo, int pageSize, String keyword) {
        List<BizPurchaseReceiptH> all = receiptHMapper.selectList(new QueryWrapper<BizPurchaseReceiptH>()
                .eq("org_id", ORG_ID).eq("del_flag", 0).orderByDesc("id"));
        if (keyword != null && !keyword.isBlank()) all = all.stream().filter(x -> (x.getReceiptNo() + "").contains(keyword)).toList();
        return page(all, pageNo, pageSize);
    }

    public Map<String, Object> receiptDetail(Long id) {
        BizPurchaseReceiptH h = mustReceipt(id);
        List<BizPurchaseReceiptD> lines = receiptDMapper.selectList(new QueryWrapper<BizPurchaseReceiptD>()
                .eq("org_id", ORG_ID).eq("receipt_id", id).eq("del_flag", 0).orderByAsc("line_no"));
        return Map.of("header", h, "lines", lines);
    }

    @Transactional
    public Map<String, Object> confirmReceipt(PurchaseDtos.ReceiptConfirmReq req) {
        BizPurchaseReceiptH h = req.getHeader();
        List<BizPurchaseReceiptD> lines = Optional.ofNullable(req.getLines()).orElse(List.of());
        if (h == null || h.getOrderId() == null || lines.isEmpty()) throw new BizException(400, "收货头/行不能为空");

        BizPurchaseOrderH order = mustOrder(h.getOrderId());
        if (!"APPROVED".equals(order.getApprovalStatus()) && !"PARTIAL".equals(order.getStatus())) {
            throw new BizException(400, "采购订单未审核通过，不可收货");
        }

        List<BizPurchaseOrderD> orderLines = orderDMapper.selectList(new QueryWrapper<BizPurchaseOrderD>()
                .eq("org_id", ORG_ID).eq("order_id", order.getId()).eq("del_flag", 0));
        Map<Long, BizPurchaseOrderD> orderLineMap = orderLines.stream().collect(Collectors.toMap(BizPurchaseOrderD::getId, x -> x));

        h.setId(null);
        h.setOrgId(ORG_ID);
        h.setReceiptNo(Optional.ofNullable(h.getReceiptNo()).filter(s -> !s.isBlank()).orElse(noGenerator.next("RC")));
        h.setBizDate(Optional.ofNullable(h.getBizDate()).orElse(LocalDate.now()));
        h.setStatus("CONFIRMED");
        h.setInboundQty(BigDecimal.ZERO);
        h.setDelFlag(0);
        receiptHMapper.insert(h);

        BigDecimal total = BigDecimal.ZERO;
        int idx = 1;
        for (BizPurchaseReceiptD line : lines) {
            BizPurchaseOrderD ol = orderLineMap.get(line.getOrderLineId());
            if (ol == null) throw new BizException(400, "订单行不存在: " + line.getOrderLineId());
            BigDecimal remain = safe(ol.getOrderQty()).subtract(safe(ol.getReceivedQty()));
            if (line.getReceiptQty() == null || line.getReceiptQty().compareTo(BigDecimal.ZERO) <= 0) throw new BizException(400, "收货数量必须大于0");
            if (line.getReceiptQty().compareTo(remain) > 0) throw new BizException(400, "收货数量不得超过采购订单剩余可收货数量");

            line.setId(null);
            line.setOrgId(ORG_ID);
            line.setReceiptId(h.getId());
            line.setLineNo(idx++);
            line.setMaterialId(ol.getMaterialId());
            line.setInboundQty(BigDecimal.ZERO);
            line.setStatus("OPEN");
            line.setDelFlag(0);
            receiptDMapper.insert(line);

            // 回写订单行
            ol.setReceivedQty(safe(ol.getReceivedQty()).add(line.getReceiptQty()));
            ol.setStatus(ol.getReceivedQty().compareTo(safe(ol.getOrderQty())) >= 0 ? "RECEIVED" : "PARTIAL");
            orderDMapper.updateById(ol);
            total = total.add(line.getReceiptQty());
        }
        h.setTotalQty(total);
        receiptHMapper.updateById(h);

        // 回写订单头
        List<BizPurchaseOrderD> latest = orderDMapper.selectList(new QueryWrapper<BizPurchaseOrderD>()
                .eq("org_id", ORG_ID).eq("order_id", order.getId()).eq("del_flag", 0));
        BigDecimal recTotal = latest.stream().map(x -> safe(x.getReceivedQty())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal ordTotal = latest.stream().map(x -> safe(x.getOrderQty())).reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setReceivedQty(recTotal);
        order.setStatus(recTotal.compareTo(ordTotal) >= 0 ? "RECEIVED" : "PARTIAL");
        orderHMapper.updateById(order);

        return receiptDetail(h.getId());
    }

    // ---------- 采购入库 ----------
    public PageResult<BizPurchaseInboundH> pageInbounds(int pageNo, int pageSize, String keyword) {
        List<BizPurchaseInboundH> all = inboundHMapper.selectList(new QueryWrapper<BizPurchaseInboundH>()
                .eq("org_id", ORG_ID).eq("del_flag", 0).orderByDesc("id"));
        if (keyword != null && !keyword.isBlank()) all = all.stream().filter(x -> (x.getInboundNo() + "").contains(keyword)).toList();
        return page(all, pageNo, pageSize);
    }

    public Map<String, Object> inboundDetail(Long id) {
        BizPurchaseInboundH h = mustInbound(id);
        List<BizPurchaseInboundD> lines = inboundDMapper.selectList(new QueryWrapper<BizPurchaseInboundD>()
                .eq("org_id", ORG_ID).eq("inbound_id", id).eq("del_flag", 0).orderByAsc("line_no"));
        return Map.of("header", h, "lines", lines);
    }

    @Transactional
    public Map<String, Object> saveInbound(PurchaseDtos.InboundSaveReq req) {
        BizPurchaseInboundH h = req.getHeader();
        List<BizPurchaseInboundD> lines = Optional.ofNullable(req.getLines()).orElse(List.of());
        if (h == null || h.getReceiptId() == null || lines.isEmpty()) throw new BizException(400, "入库头/行不能为空");

        List<BizPurchaseReceiptD> receiptLines = receiptDMapper.selectList(new QueryWrapper<BizPurchaseReceiptD>()
                .eq("org_id", ORG_ID).eq("receipt_id", h.getReceiptId()).eq("del_flag", 0));
        Map<Long, BizPurchaseReceiptD> receiptLineMap = receiptLines.stream().collect(Collectors.toMap(BizPurchaseReceiptD::getId, x -> x));

        if (h.getId() == null) {
            h.setOrgId(ORG_ID);
            h.setInboundNo(Optional.ofNullable(h.getInboundNo()).filter(s -> !s.isBlank()).orElse(noGenerator.next("PI")));
            h.setBizDate(Optional.ofNullable(h.getBizDate()).orElse(LocalDate.now()));
            h.setStatus("DRAFT");
            h.setQcRequired(Optional.ofNullable(h.getQcRequired()).orElse(0));
            h.setQcPassed(Optional.ofNullable(h.getQcPassed()).orElse(0));
            h.setInspectionHookStatus(h.getQcRequired() == 1 ? "PENDING" : "BYPASS");
            h.setTotalQty(sumInboundQty(lines));
            h.setDelFlag(0);
            inboundHMapper.insert(h);
        } else {
            BizPurchaseInboundH old = mustInbound(h.getId());
            if ("APPROVED".equals(old.getStatus())) throw new BizException(400, "已审核入库不可编辑");
            old.setReceiptId(h.getReceiptId());
            old.setBizDate(Optional.ofNullable(h.getBizDate()).orElse(old.getBizDate()));
            old.setQcRequired(Optional.ofNullable(h.getQcRequired()).orElse(old.getQcRequired()));
            old.setQcPassed(Optional.ofNullable(h.getQcPassed()).orElse(old.getQcPassed()));
            old.setInspectionHookStatus(old.getQcRequired() == 1 ? "PENDING" : "BYPASS");
            old.setRemark(h.getRemark());
            old.setTotalQty(sumInboundQty(lines));
            inboundHMapper.updateById(old);
            h = old;
            inboundDMapper.delete(new QueryWrapper<BizPurchaseInboundD>().eq("org_id", ORG_ID).eq("inbound_id", h.getId()));
        }

        int idx = 1;
        for (BizPurchaseInboundD line : lines) {
            BizPurchaseReceiptD rl = receiptLineMap.get(line.getReceiptLineId());
            if (rl == null) throw new BizException(400, "收货行不存在: " + line.getReceiptLineId());
            BigDecimal remain = safe(rl.getReceiptQty()).subtract(safe(rl.getInboundQty()));
            if (line.getInboundQty() == null || line.getInboundQty().compareTo(BigDecimal.ZERO) <= 0) throw new BizException(400, "入库数量必须大于0");
            if (line.getInboundQty().compareTo(remain) > 0) throw new BizException(400, "入库数量不得超过收货剩余可入库数量");

            line.setId(null);
            line.setOrgId(ORG_ID);
            line.setInboundId(h.getId());
            line.setLineNo(idx++);
            line.setMaterialId(rl.getMaterialId());
            line.setStatus("OPEN");
            line.setDelFlag(0);
            inboundDMapper.insert(line);
        }

        return inboundDetail(h.getId());
    }

    @Transactional
    public BizPurchaseInboundH auditInbound(Long id, PurchaseDtos.AuditReq req) {
        BizPurchaseInboundH h = mustInbound(id);
        boolean ok = Boolean.TRUE.equals(req.getApproved());
        if (!ok) {
            h.setStatus("REJECTED");
            inboundHMapper.updateById(h);
            return h;
        }

        boolean canInbound = qualityInspectionStubService.canInbound(id, h.getQcRequired() == 1, h.getQcPassed() == 1);
        if (!canInbound) throw new BizException(400, "启用质检时，需检验通过后方可入库");

        List<BizPurchaseInboundD> inLines = inboundDMapper.selectList(new QueryWrapper<BizPurchaseInboundD>()
                .eq("org_id", ORG_ID).eq("inbound_id", id).eq("del_flag", 0));

        Map<Long, BigDecimal> receiptDelta = new HashMap<>();
        Map<Long, BigDecimal> orderDelta = new HashMap<>();

        for (BizPurchaseInboundD inLine : inLines) {
            BizPurchaseReceiptD rl = receiptDMapper.selectById(inLine.getReceiptLineId());
            if (rl == null || rl.getDelFlag() != 0) throw new BizException(400, "收货行不存在");
            BigDecimal remain = safe(rl.getReceiptQty()).subtract(safe(rl.getInboundQty()));
            if (inLine.getInboundQty().compareTo(remain) > 0) throw new BizException(400, "审核失败：入库数量超过收货剩余");

            receiptDelta.merge(rl.getId(), inLine.getInboundQty(), BigDecimal::add);
            orderDelta.merge(rl.getOrderLineId(), inLine.getInboundQty(), BigDecimal::add);
        }

        // 回写收货行
        for (Map.Entry<Long, BigDecimal> e : receiptDelta.entrySet()) {
            BizPurchaseReceiptD rl = receiptDMapper.selectById(e.getKey());
            rl.setInboundQty(safe(rl.getInboundQty()).add(e.getValue()));
            rl.setStatus(rl.getInboundQty().compareTo(safe(rl.getReceiptQty())) >= 0 ? "DONE" : "PARTIAL");
            receiptDMapper.updateById(rl);
        }

        // 回写订单行
        for (Map.Entry<Long, BigDecimal> e : orderDelta.entrySet()) {
            BizPurchaseOrderD ol = orderDMapper.selectById(e.getKey());
            ol.setInboundQty(safe(ol.getInboundQty()).add(e.getValue()));
            ol.setStatus(ol.getInboundQty().compareTo(safe(ol.getOrderQty())) >= 0 ? "DONE" : "PARTIAL");
            orderDMapper.updateById(ol);
        }

        // 回写收货头
        BizPurchaseReceiptH rh = mustReceipt(h.getReceiptId());
        List<BizPurchaseReceiptD> rhLines = receiptDMapper.selectList(new QueryWrapper<BizPurchaseReceiptD>()
                .eq("org_id", ORG_ID).eq("receipt_id", rh.getId()).eq("del_flag", 0));
        BigDecimal receiptInbound = rhLines.stream().map(x -> safe(x.getInboundQty())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal receiptTotal = rhLines.stream().map(x -> safe(x.getReceiptQty())).reduce(BigDecimal.ZERO, BigDecimal::add);
        rh.setInboundQty(receiptInbound);
        rh.setStatus(receiptInbound.compareTo(receiptTotal) >= 0 ? "DONE" : "PARTIAL");
        receiptHMapper.updateById(rh);

        // 回写订单头
        BizPurchaseOrderH oh = mustOrder(rh.getOrderId());
        List<BizPurchaseOrderD> ohLines = orderDMapper.selectList(new QueryWrapper<BizPurchaseOrderD>()
                .eq("org_id", ORG_ID).eq("order_id", oh.getId()).eq("del_flag", 0));
        BigDecimal inboundTotal = ohLines.stream().map(x -> safe(x.getInboundQty())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal orderTotal = ohLines.stream().map(x -> safe(x.getOrderQty())).reduce(BigDecimal.ZERO, BigDecimal::add);
        oh.setInboundQty(inboundTotal);
        oh.setStatus(inboundTotal.compareTo(orderTotal) >= 0 ? "DONE" : "PARTIAL");
        orderHMapper.updateById(oh);

        // 回写申请（如果由申请下推）
        if (oh.getReqId() != null) {
            BizPurchaseReqH reqH = mustReq(oh.getReqId());
            List<BizPurchaseReqD> reqLines = reqDMapper.selectList(new QueryWrapper<BizPurchaseReqD>()
                    .eq("org_id", ORG_ID).eq("req_id", reqH.getId()).eq("del_flag", 0));
            BigDecimal reqOrdered = reqLines.stream().map(x -> safe(x.getOrderedQty())).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal reqTotal = reqLines.stream().map(x -> safe(x.getReqQty())).reduce(BigDecimal.ZERO, BigDecimal::add);
            reqH.setOrderedQty(reqOrdered);
            reqH.setStatus(reqOrdered.compareTo(reqTotal) >= 0 ? "DONE" : "PARTIAL");
            reqHMapper.updateById(reqH);
        }

        h.setStatus("APPROVED");
        h.setInspectionHookStatus(h.getQcRequired() == 1 ? "PASSED" : "BYPASS");
        inboundHMapper.updateById(h);

        // 入库审核后写库存台账
        inventoryInboundStubService.writeLedgerForPurchaseInbound(h.getId());

        return h;
    }

    @Transactional
    public BizPurchaseInboundH unAuditInbound(Long id) {
        BizPurchaseInboundH h = mustInbound(id);
        if (!"APPROVED".equals(h.getStatus())) throw new BizException(400, "仅已审核入库单可反审核");

        List<BizPurchaseInboundD> inLines = inboundDMapper.selectList(new QueryWrapper<BizPurchaseInboundD>()
                .eq("org_id", ORG_ID).eq("inbound_id", id).eq("del_flag", 0));

        Map<Long, BigDecimal> receiptDelta = new HashMap<>();
        Map<Long, BigDecimal> orderDelta = new HashMap<>();
        for (BizPurchaseInboundD inLine : inLines) {
            receiptDelta.merge(inLine.getReceiptLineId(), safe(inLine.getInboundQty()), BigDecimal::add);
            BizPurchaseReceiptD rl = receiptDMapper.selectById(inLine.getReceiptLineId());
            if (rl != null && rl.getOrderLineId() != null) {
                orderDelta.merge(rl.getOrderLineId(), safe(inLine.getInboundQty()), BigDecimal::add);
            }
        }

        // 先回滚库存与台账
        inventoryInboundStubService.rollbackForPurchaseInbound(h.getId());

        // 回写收货行
        for (Map.Entry<Long, BigDecimal> e : receiptDelta.entrySet()) {
            BizPurchaseReceiptD rl = receiptDMapper.selectById(e.getKey());
            if (rl == null || rl.getDelFlag() != 0) continue;
            BigDecimal next = safe(rl.getInboundQty()).subtract(e.getValue());
            if (next.compareTo(BigDecimal.ZERO) < 0) next = BigDecimal.ZERO;
            rl.setInboundQty(next);
            rl.setStatus(next.compareTo(BigDecimal.ZERO) == 0 ? "OPEN" : (next.compareTo(safe(rl.getReceiptQty())) >= 0 ? "DONE" : "PARTIAL"));
            receiptDMapper.updateById(rl);
        }

        // 回写订单行
        for (Map.Entry<Long, BigDecimal> e : orderDelta.entrySet()) {
            BizPurchaseOrderD ol = orderDMapper.selectById(e.getKey());
            if (ol == null || ol.getDelFlag() != 0) continue;
            BigDecimal next = safe(ol.getInboundQty()).subtract(e.getValue());
            if (next.compareTo(BigDecimal.ZERO) < 0) next = BigDecimal.ZERO;
            ol.setInboundQty(next);
            ol.setStatus(next.compareTo(BigDecimal.ZERO) == 0 ? "OPEN" : (next.compareTo(safe(ol.getOrderQty())) >= 0 ? "DONE" : "PARTIAL"));
            orderDMapper.updateById(ol);
        }

        // 回写收货头
        BizPurchaseReceiptH rh = mustReceipt(h.getReceiptId());
        List<BizPurchaseReceiptD> rhLines = receiptDMapper.selectList(new QueryWrapper<BizPurchaseReceiptD>()
                .eq("org_id", ORG_ID).eq("receipt_id", rh.getId()).eq("del_flag", 0));
        BigDecimal receiptInbound = rhLines.stream().map(x -> safe(x.getInboundQty())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal receiptTotal = rhLines.stream().map(x -> safe(x.getReceiptQty())).reduce(BigDecimal.ZERO, BigDecimal::add);
        rh.setInboundQty(receiptInbound);
        rh.setStatus(receiptInbound.compareTo(BigDecimal.ZERO) == 0 ? "CONFIRMED" : (receiptInbound.compareTo(receiptTotal) >= 0 ? "DONE" : "PARTIAL"));
        receiptHMapper.updateById(rh);

        // 回写订单头
        BizPurchaseOrderH oh = mustOrder(rh.getOrderId());
        List<BizPurchaseOrderD> ohLines = orderDMapper.selectList(new QueryWrapper<BizPurchaseOrderD>()
                .eq("org_id", ORG_ID).eq("order_id", oh.getId()).eq("del_flag", 0));
        BigDecimal inboundTotal = ohLines.stream().map(x -> safe(x.getInboundQty())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal orderTotal = ohLines.stream().map(x -> safe(x.getOrderQty())).reduce(BigDecimal.ZERO, BigDecimal::add);
        oh.setInboundQty(inboundTotal);
        oh.setStatus(inboundTotal.compareTo(BigDecimal.ZERO) == 0 ? "APPROVED" : (inboundTotal.compareTo(orderTotal) >= 0 ? "DONE" : "PARTIAL"));
        orderHMapper.updateById(oh);

        h.setStatus("DRAFT");
        h.setInspectionHookStatus(h.getQcRequired() == 1 ? "PENDING" : "BYPASS");
        inboundHMapper.updateById(h);
        return h;
    }

    // ---------- 采购退货（骨架） ----------
    public PageResult<BizPurchaseReturnH> pageReturns(int pageNo, int pageSize, String keyword) {
        List<BizPurchaseReturnH> all = returnHMapper.selectList(new QueryWrapper<BizPurchaseReturnH>()
                .eq("org_id", ORG_ID).eq("del_flag", 0).orderByDesc("id"));
        if (keyword != null && !keyword.isBlank()) all = all.stream().filter(x -> (x.getReturnNo() + "").contains(keyword)).toList();
        return page(all, pageNo, pageSize);
    }

    public Map<String, Object> returnDetail(Long id) {
        BizPurchaseReturnH h = mustReturn(id);
        List<BizPurchaseReturnD> lines = returnDMapper.selectList(new QueryWrapper<BizPurchaseReturnD>()
                .eq("org_id", ORG_ID).eq("return_id", id).eq("del_flag", 0).orderByAsc("line_no"));
        return Map.of("header", h, "lines", lines);
    }

    @Transactional
    public Map<String, Object> saveReturn(PurchaseDtos.PurchaseReturnSaveReq req) {
        BizPurchaseReturnH h = req.getHeader();
        List<BizPurchaseReturnD> lines = Optional.ofNullable(req.getLines()).orElse(List.of());
        if (h == null || lines.isEmpty()) throw new BizException(400, "采购退货头/行不能为空");

        if (h.getId() == null) {
            h.setOrgId(ORG_ID);
            h.setReturnNo(Optional.ofNullable(h.getReturnNo()).filter(s -> !s.isBlank()).orElse(noGenerator.next("RT")));
            h.setBizDate(Optional.ofNullable(h.getBizDate()).orElse(LocalDate.now()));
            h.setStatus("DRAFT");
            h.setApprovalStatus("DRAFT");
            h.setTotalQty(sumReturnQty(lines));
            h.setDelFlag(0);
            returnHMapper.insert(h);
        } else {
            BizPurchaseReturnH old = mustReturn(h.getId());
            old.setSupplierId(h.getSupplierId());
            old.setBizDate(Optional.ofNullable(h.getBizDate()).orElse(old.getBizDate()));
            old.setRemark(h.getRemark());
            old.setTotalQty(sumReturnQty(lines));
            returnHMapper.updateById(old);
            h = old;
            returnDMapper.delete(new QueryWrapper<BizPurchaseReturnD>().eq("org_id", ORG_ID).eq("return_id", h.getId()));
        }

        int idx = 1;
        for (BizPurchaseReturnD line : lines) {
            line.setId(null);
            line.setOrgId(ORG_ID);
            line.setReturnId(h.getId());
            line.setLineNo(idx++);
            line.setDelFlag(0);
            if (line.getReturnQty() == null || line.getReturnQty().compareTo(BigDecimal.ZERO) <= 0) throw new BizException(400, "退货数量必须大于0");
            returnDMapper.insert(line);
        }

        return returnDetail(h.getId());
    }

    public BizPurchaseReturnH auditReturn(Long id, PurchaseDtos.AuditReq req) {
        BizPurchaseReturnH h = mustReturn(id);
        boolean ok = Boolean.TRUE.equals(req.getApproved());
        h.setApprovalStatus(ok ? "APPROVED" : "REJECTED");
        h.setStatus(ok ? "APPROVED" : "REJECTED");
        returnHMapper.updateById(h);
        return h;
    }

    // ---------- helpers ----------
    private BizPurchaseReqH mustReq(Long id) {
        BizPurchaseReqH x = reqHMapper.selectById(id);
        if (x == null || x.getDelFlag() != 0) throw new BizException(404, "采购申请不存在");
        return x;
    }

    private BizPurchaseOrderH mustOrder(Long id) {
        BizPurchaseOrderH x = orderHMapper.selectById(id);
        if (x == null || x.getDelFlag() != 0) throw new BizException(404, "采购订单不存在");
        return x;
    }

    private BizPurchaseReceiptH mustReceipt(Long id) {
        BizPurchaseReceiptH x = receiptHMapper.selectById(id);
        if (x == null || x.getDelFlag() != 0) throw new BizException(404, "收货单不存在");
        return x;
    }

    private BizPurchaseInboundH mustInbound(Long id) {
        BizPurchaseInboundH x = inboundHMapper.selectById(id);
        if (x == null || x.getDelFlag() != 0) throw new BizException(404, "采购入库不存在");
        return x;
    }

    private BizPurchaseReturnH mustReturn(Long id) {
        BizPurchaseReturnH x = returnHMapper.selectById(id);
        if (x == null || x.getDelFlag() != 0) throw new BizException(404, "采购退货不存在");
        return x;
    }

    private BigDecimal safe(BigDecimal x) { return x == null ? BigDecimal.ZERO : x; }

    private BigDecimal sumReqQty(List<BizPurchaseReqD> lines) { return lines.stream().map(x -> safe(x.getReqQty())).reduce(BigDecimal.ZERO, BigDecimal::add); }
    private BigDecimal sumOrderQty(List<BizPurchaseOrderD> lines) { return lines.stream().map(x -> safe(x.getOrderQty())).reduce(BigDecimal.ZERO, BigDecimal::add); }
    private BigDecimal sumInboundQty(List<BizPurchaseInboundD> lines) { return lines.stream().map(x -> safe(x.getInboundQty())).reduce(BigDecimal.ZERO, BigDecimal::add); }
    private BigDecimal sumReturnQty(List<BizPurchaseReturnD> lines) { return lines.stream().map(x -> safe(x.getReturnQty())).reduce(BigDecimal.ZERO, BigDecimal::add); }

    private <T> PageResult<T> page(List<T> all, int pageNo, int pageSize) {
        int from = Math.max(0, (pageNo - 1) * pageSize);
        int to = Math.min(all.size(), from + pageSize);
        List<T> data = from >= all.size() ? List.of() : all.subList(from, to);
        return PageResult.of(all.size(), pageNo, pageSize, data);
    }
}
