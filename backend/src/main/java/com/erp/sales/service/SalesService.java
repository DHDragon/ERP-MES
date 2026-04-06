package com.erp.sales.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.erp.common.dto.PageResult;
import com.erp.common.exception.BizException;
import com.erp.sales.dto.SalesDtos;
import com.erp.sales.entity.*;
import com.erp.sales.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalesService {

    private static final long ORG_ID = 1L;

    private final BizSalesOrderHMapper orderHMapper;
    private final BizSalesOrderDMapper orderDMapper;
    private final BizDeliveryNoticeHMapper noticeHMapper;
    private final BizDeliveryNoticeDMapper noticeDMapper;
    private final BizSalesOutboundHMapper outboundHMapper;
    private final BizSalesOutboundDMapper outboundDMapper;
    private final BizSalesReturnHMapper returnHMapper;
    private final BizSalesReturnDMapper returnDMapper;
    private final InventoryStubService inventoryStubService;
    private final SalesNoGenerator noGenerator;

    public SalesService(BizSalesOrderHMapper orderHMapper,
                        BizSalesOrderDMapper orderDMapper,
                        BizDeliveryNoticeHMapper noticeHMapper,
                        BizDeliveryNoticeDMapper noticeDMapper,
                        BizSalesOutboundHMapper outboundHMapper,
                        BizSalesOutboundDMapper outboundDMapper,
                        BizSalesReturnHMapper returnHMapper,
                        BizSalesReturnDMapper returnDMapper,
                        InventoryStubService inventoryStubService,
                        SalesNoGenerator noGenerator) {
        this.orderHMapper = orderHMapper;
        this.orderDMapper = orderDMapper;
        this.noticeHMapper = noticeHMapper;
        this.noticeDMapper = noticeDMapper;
        this.outboundHMapper = outboundHMapper;
        this.outboundDMapper = outboundDMapper;
        this.returnHMapper = returnHMapper;
        this.returnDMapper = returnDMapper;
        this.inventoryStubService = inventoryStubService;
        this.noGenerator = noGenerator;
    }

    // ------------------ 销售订单 ------------------
    public PageResult<BizSalesOrderH> pageSalesOrders(int pageNo, int pageSize, String keyword) {
        List<BizSalesOrderH> all = orderHMapper.selectList(new QueryWrapper<BizSalesOrderH>()
                .eq("org_id", ORG_ID).eq("del_flag", 0).orderByDesc("id"));
        if (keyword != null && !keyword.isBlank()) {
            all = all.stream().filter(x -> (x.getOrderNo() + "").contains(keyword)).toList();
        }
        return page(all, pageNo, pageSize);
    }

    public Map<String, Object> salesOrderDetail(Long id) {
        BizSalesOrderH h = mustOrder(id);
        List<BizSalesOrderD> lines = orderDMapper.selectList(new QueryWrapper<BizSalesOrderD>().eq("org_id", ORG_ID).eq("order_id", id).eq("del_flag", 0).orderByAsc("line_no"));
        return Map.of("header", h, "lines", lines);
    }

    @Transactional
    public Map<String, Object> saveSalesOrder(SalesDtos.SalesOrderSaveReq req) {
        BizSalesOrderH h = req.getHeader();
        if (h == null) throw new BizException(400, "header不能为空");
        List<BizSalesOrderD> lines = Optional.ofNullable(req.getLines()).orElse(List.of());
        if (lines.isEmpty()) throw new BizException(400, "订单行不能为空");

        if (h.getId() == null) {
            h.setOrgId(ORG_ID);
            h.setOrderNo(Optional.ofNullable(h.getOrderNo()).filter(s -> !s.isBlank()).orElse(noGenerator.next("SO")));
            h.setStatus("DRAFT");
            h.setApprovalStatus("DRAFT");
            h.setBizDate(Optional.ofNullable(h.getBizDate()).orElse(LocalDate.now()));
            h.setDeliveredQty(BigDecimal.ZERO);
            h.setDelFlag(0);
            h.setTotalQty(sumOrderQty(lines));
            orderHMapper.insert(h);
        } else {
            BizSalesOrderH old = mustOrder(h.getId());
            if (Set.of("CLOSED", "VOID").contains(old.getStatus())) throw new BizException(400, "已关闭/作废订单不可编辑");
            old.setCustomerId(h.getCustomerId());
            old.setRemark(h.getRemark());
            old.setBizDate(Optional.ofNullable(h.getBizDate()).orElse(old.getBizDate()));
            old.setTotalQty(sumOrderQty(lines));
            orderHMapper.updateById(old);
            h = old;
            orderDMapper.delete(new QueryWrapper<BizSalesOrderD>().eq("org_id", ORG_ID).eq("order_id", h.getId()));
        }

        int idx = 1;
        for (BizSalesOrderD line : lines) {
            line.setId(null);
            line.setOrgId(ORG_ID);
            line.setOrderId(h.getId());
            line.setLineNo(idx++);
            line.setDeliveredQty(BigDecimal.ZERO);
            line.setStatus("OPEN");
            line.setDelFlag(0);
            if (line.getOrderQty() == null || line.getOrderQty().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BizException(400, "订单行数量必须大于0");
            }
            orderDMapper.insert(line);
        }

        return salesOrderDetail(h.getId());
    }

    public BizSalesOrderH submitSalesOrder(Long id) {
        BizSalesOrderH h = mustOrder(id);
        h.setApprovalStatus("PENDING");
        h.setStatus("SUBMITTED");
        orderHMapper.updateById(h);
        return h;
    }

    public BizSalesOrderH auditSalesOrder(Long id, SalesDtos.AuditReq req) {
        BizSalesOrderH h = mustOrder(id);
        boolean ok = Boolean.TRUE.equals(req.getApproved());
        h.setApprovalStatus(ok ? "APPROVED" : "REJECTED");
        h.setStatus(ok ? "APPROVED" : "REJECTED");
        orderHMapper.updateById(h);
        return h;
    }

    public BizSalesOrderH unAuditSalesOrder(Long id) {
        BizSalesOrderH h = mustOrder(id);
        if (h.getDeliveredQty() != null && h.getDeliveredQty().compareTo(BigDecimal.ZERO) > 0) {
            throw new BizException(400, "已有下游执行，不可反审核");
        }
        h.setApprovalStatus("SUBMITTED");
        h.setStatus("SUBMITTED");
        orderHMapper.updateById(h);
        return h;
    }

    public BizSalesOrderH closeSalesOrder(Long id) {
        BizSalesOrderH h = mustOrder(id);
        h.setStatus("CLOSED");
        orderHMapper.updateById(h);
        return h;
    }

    public BizSalesOrderH voidSalesOrder(Long id) {
        BizSalesOrderH h = mustOrder(id);
        if (h.getDeliveredQty() != null && h.getDeliveredQty().compareTo(BigDecimal.ZERO) > 0) {
            throw new BizException(400, "已有下游执行，不可作废");
        }
        h.setStatus("VOID");
        orderHMapper.updateById(h);
        return h;
    }

    @Transactional
    public Map<String, Object> pushToDeliveryNotice(SalesDtos.PushNoticeReq req) {
        Long orderId = req.getOrderId();
        BizSalesOrderH order = mustOrder(orderId);
        if (!"APPROVED".equals(order.getApprovalStatus())) {
            throw new BizException(400, "销售订单未审核，不得下推发货通知");
        }
        List<BizSalesOrderD> orderLines = orderDMapper.selectList(new QueryWrapper<BizSalesOrderD>().eq("org_id", ORG_ID).eq("order_id", orderId).eq("del_flag", 0));
        Map<Long, BizSalesOrderD> orderLineMap = orderLines.stream().collect(Collectors.toMap(BizSalesOrderD::getId, x -> x));

        List<SalesDtos.PushLine> pushLines = Optional.ofNullable(req.getLines()).orElse(List.of());
        if (pushLines.isEmpty()) throw new BizException(400, "下推行不能为空");

        BizDeliveryNoticeH nh = new BizDeliveryNoticeH();
        nh.setOrgId(ORG_ID);
        nh.setNoticeNo(noGenerator.next("DN"));
        nh.setOrderId(orderId);
        nh.setBizDate(Optional.ofNullable(req.getBizDate()).orElse(LocalDate.now()));
        nh.setStatus("OPEN");
        nh.setOutboundQty(BigDecimal.ZERO);
        nh.setDelFlag(0);
        noticeHMapper.insert(nh);

        BigDecimal total = BigDecimal.ZERO;
        int idx = 1;
        for (SalesDtos.PushLine pl : pushLines) {
            BizSalesOrderD ol = orderLineMap.get(pl.getSourceLineId());
            if (ol == null) throw new BizException(400, "订单行不存在: " + pl.getSourceLineId());
            BigDecimal remain = safe(ol.getOrderQty()).subtract(safe(ol.getDeliveredQty()));
            if (pl.getQty() == null || pl.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BizException(400, "下推数量必须大于0");
            }
            if (pl.getQty().compareTo(remain) > 0) {
                throw new BizException(400, "发货通知数量不得超过订单未发数量");
            }
            BizDeliveryNoticeD nd = new BizDeliveryNoticeD();
            nd.setOrgId(ORG_ID);
            nd.setNoticeId(nh.getId());
            nd.setLineNo(idx++);
            nd.setOrderLineId(ol.getId());
            nd.setMaterialId(ol.getMaterialId());
            nd.setNoticeQty(pl.getQty());
            nd.setOutboundQty(BigDecimal.ZERO);
            nd.setStatus("OPEN");
            nd.setDelFlag(0);
            noticeDMapper.insert(nd);
            total = total.add(pl.getQty());
        }
        nh.setTotalQty(total);
        noticeHMapper.updateById(nh);

        return deliveryNoticeDetail(nh.getId());
    }

    // ------------------ 发货通知 ------------------
    public PageResult<BizDeliveryNoticeH> pageDeliveryNotices(int pageNo, int pageSize, String keyword) {
        List<BizDeliveryNoticeH> all = noticeHMapper.selectList(new QueryWrapper<BizDeliveryNoticeH>().eq("org_id", ORG_ID).eq("del_flag", 0).orderByDesc("id"));
        if (keyword != null && !keyword.isBlank()) all = all.stream().filter(x -> (x.getNoticeNo() + "").contains(keyword)).toList();
        return page(all, pageNo, pageSize);
    }

    public Map<String, Object> deliveryNoticeDetail(Long id) {
        BizDeliveryNoticeH h = mustNotice(id);
        List<BizDeliveryNoticeD> lines = noticeDMapper.selectList(new QueryWrapper<BizDeliveryNoticeD>().eq("org_id", ORG_ID).eq("notice_id", id).eq("del_flag", 0).orderByAsc("line_no"));
        return Map.of("header", h, "lines", lines);
    }

    @Transactional
    public Map<String, Object> saveDeliveryNotice(SalesDtos.DeliveryNoticeSaveReq req) {
        BizDeliveryNoticeH h = req.getHeader();
        List<BizDeliveryNoticeD> lines = Optional.ofNullable(req.getLines()).orElse(List.of());
        if (h == null || lines.isEmpty()) throw new BizException(400, "通知头/行不能为空");

        if (h.getId() == null) {
            h.setOrgId(ORG_ID);
            h.setNoticeNo(Optional.ofNullable(h.getNoticeNo()).filter(s -> !s.isBlank()).orElse(noGenerator.next("DN")));
            h.setBizDate(Optional.ofNullable(h.getBizDate()).orElse(LocalDate.now()));
            h.setStatus("OPEN");
            h.setOutboundQty(BigDecimal.ZERO);
            h.setTotalQty(sumNoticeQty(lines));
            h.setDelFlag(0);
            noticeHMapper.insert(h);
        } else {
            BizDeliveryNoticeH old = mustNotice(h.getId());
            if ("CLOSED".equals(old.getStatus())) throw new BizException(400, "已关闭通知不可编辑");
            old.setOrderId(h.getOrderId());
            old.setRemark(h.getRemark());
            old.setBizDate(Optional.ofNullable(h.getBizDate()).orElse(old.getBizDate()));
            old.setTotalQty(sumNoticeQty(lines));
            noticeHMapper.updateById(old);
            h = old;
            noticeDMapper.delete(new QueryWrapper<BizDeliveryNoticeD>().eq("org_id", ORG_ID).eq("notice_id", h.getId()));
        }

        int idx = 1;
        for (BizDeliveryNoticeD line : lines) {
            line.setId(null);
            line.setOrgId(ORG_ID);
            line.setNoticeId(h.getId());
            line.setLineNo(idx++);
            line.setOutboundQty(BigDecimal.ZERO);
            line.setStatus("OPEN");
            line.setDelFlag(0);
            if (line.getNoticeQty() == null || line.getNoticeQty().compareTo(BigDecimal.ZERO) <= 0) throw new BizException(400, "通知数量必须大于0");

            if (line.getOrderLineId() != null) {
                BizSalesOrderD ol = orderDMapper.selectById(line.getOrderLineId());
                if (ol == null || ol.getDelFlag() != 0) throw new BizException(400, "源订单行不存在");
                BigDecimal remain = safe(ol.getOrderQty()).subtract(safe(ol.getDeliveredQty()));
                if (line.getNoticeQty().compareTo(remain) > 0) throw new BizException(400, "发货通知数量不得超过订单未发数量");
                line.setMaterialId(ol.getMaterialId());
            }
            noticeDMapper.insert(line);
        }
        return deliveryNoticeDetail(h.getId());
    }

    public BizDeliveryNoticeH auditDeliveryNotice(Long id) {
        BizDeliveryNoticeH h = mustNotice(id);
        h.setStatus("APPROVED");
        noticeHMapper.updateById(h);
        return h;
    }

    public BizDeliveryNoticeH closeDeliveryNotice(Long id) {
        BizDeliveryNoticeH h = mustNotice(id);
        h.setStatus("CLOSED");
        noticeHMapper.updateById(h);
        return h;
    }

    // ------------------ 销售出库 ------------------
    public PageResult<BizSalesOutboundH> pageSalesOutbounds(int pageNo, int pageSize, String keyword) {
        List<BizSalesOutboundH> all = outboundHMapper.selectList(new QueryWrapper<BizSalesOutboundH>().eq("org_id", ORG_ID).eq("del_flag", 0).orderByDesc("id"));
        if (keyword != null && !keyword.isBlank()) all = all.stream().filter(x -> (x.getOutboundNo() + "").contains(keyword)).toList();
        return page(all, pageNo, pageSize);
    }

    public Map<String, Object> salesOutboundDetail(Long id) {
        BizSalesOutboundH h = mustOutbound(id);
        List<BizSalesOutboundD> lines = outboundDMapper.selectList(new QueryWrapper<BizSalesOutboundD>().eq("org_id", ORG_ID).eq("outbound_id", id).eq("del_flag", 0).orderByAsc("line_no"));
        return Map.of("header", h, "lines", lines);
    }

    @Transactional
    public Map<String, Object> saveSalesOutbound(SalesDtos.SalesOutboundSaveReq req) {
        BizSalesOutboundH h = req.getHeader();
        List<BizSalesOutboundD> lines = Optional.ofNullable(req.getLines()).orElse(List.of());
        if (h == null || lines.isEmpty()) throw new BizException(400, "出库头/行不能为空");

        if (h.getId() == null) {
            h.setOrgId(ORG_ID);
            h.setOutboundNo(Optional.ofNullable(h.getOutboundNo()).filter(s -> !s.isBlank()).orElse(noGenerator.next("SOO")));
            h.setBizDate(Optional.ofNullable(h.getBizDate()).orElse(LocalDate.now()));
            h.setStatus("DRAFT");
            h.setTotalQty(sumOutboundQty(lines));
            h.setDelFlag(0);
            outboundHMapper.insert(h);
        } else {
            BizSalesOutboundH old = mustOutbound(h.getId());
            if ("APPROVED".equals(old.getStatus())) throw new BizException(400, "已审核出库不可编辑");
            old.setNoticeId(h.getNoticeId());
            old.setRemark(h.getRemark());
            old.setBizDate(Optional.ofNullable(h.getBizDate()).orElse(old.getBizDate()));
            old.setTotalQty(sumOutboundQty(lines));
            outboundHMapper.updateById(old);
            h = old;
            outboundDMapper.delete(new QueryWrapper<BizSalesOutboundD>().eq("org_id", ORG_ID).eq("outbound_id", h.getId()));
        }

        List<BizDeliveryNoticeD> sourceNoticeLines = noticeDMapper.selectList(new QueryWrapper<BizDeliveryNoticeD>().eq("org_id", ORG_ID).eq("notice_id", h.getNoticeId()).eq("del_flag", 0));
        Map<Long, BizDeliveryNoticeD> noticeMap = sourceNoticeLines.stream().collect(Collectors.toMap(BizDeliveryNoticeD::getId, x -> x));

        int idx = 1;
        for (BizSalesOutboundD line : lines) {
            BizDeliveryNoticeD nd = noticeMap.get(line.getNoticeLineId());
            if (nd == null) throw new BizException(400, "源发货通知行不存在");
            BigDecimal remain = safe(nd.getNoticeQty()).subtract(safe(nd.getOutboundQty()));
            if (line.getOutboundQty() == null || line.getOutboundQty().compareTo(BigDecimal.ZERO) <= 0) throw new BizException(400, "出库数量必须大于0");
            if (line.getOutboundQty().compareTo(remain) > 0) throw new BizException(400, "销售出库数量不得超过发货通知剩余数量");

            line.setId(null);
            line.setOrgId(ORG_ID);
            line.setOutboundId(h.getId());
            line.setLineNo(idx++);
            line.setMaterialId(nd.getMaterialId());
            line.setStatus("OPEN");
            line.setDelFlag(0);
            outboundDMapper.insert(line);
        }
        return salesOutboundDetail(h.getId());
    }

    @Transactional
    public BizSalesOutboundH auditSalesOutbound(Long id) {
        BizSalesOutboundH h = mustOutbound(id);
        if ("APPROVED".equals(h.getStatus())) return h;

        List<BizSalesOutboundD> outLines = outboundDMapper.selectList(new QueryWrapper<BizSalesOutboundD>().eq("org_id", ORG_ID).eq("outbound_id", id).eq("del_flag", 0));

        Map<Long, BigDecimal> noticeDelta = new HashMap<>();
        Map<Long, BigDecimal> orderDelta = new HashMap<>();

        for (BizSalesOutboundD od : outLines) {
            BizDeliveryNoticeD nd = noticeDMapper.selectById(od.getNoticeLineId());
            if (nd == null || nd.getDelFlag() != 0) throw new BizException(400, "发货通知行不存在");
            BigDecimal remain = safe(nd.getNoticeQty()).subtract(safe(nd.getOutboundQty()));
            if (od.getOutboundQty().compareTo(remain) > 0) throw new BizException(400, "审核失败：出库数量超过通知剩余");

            noticeDelta.merge(nd.getId(), od.getOutboundQty(), BigDecimal::add);
            orderDelta.merge(nd.getOrderLineId(), od.getOutboundQty(), BigDecimal::add);
        }

        // 回写发货通知行
        for (Map.Entry<Long, BigDecimal> e : noticeDelta.entrySet()) {
            BizDeliveryNoticeD nd = noticeDMapper.selectById(e.getKey());
            nd.setOutboundQty(safe(nd.getOutboundQty()).add(e.getValue()));
            nd.setStatus(nd.getOutboundQty().compareTo(safe(nd.getNoticeQty())) >= 0 ? "DONE" : "PARTIAL");
            noticeDMapper.updateById(nd);
        }

        // 回写订单行
        for (Map.Entry<Long, BigDecimal> e : orderDelta.entrySet()) {
            BizSalesOrderD ol = orderDMapper.selectById(e.getKey());
            ol.setDeliveredQty(safe(ol.getDeliveredQty()).add(e.getValue()));
            ol.setStatus(ol.getDeliveredQty().compareTo(safe(ol.getOrderQty())) >= 0 ? "DONE" : "PARTIAL");
            orderDMapper.updateById(ol);
        }

        // 回写发货通知头状态
        BizDeliveryNoticeH nh = mustNotice(h.getNoticeId());
        List<BizDeliveryNoticeD> nLines = noticeDMapper.selectList(new QueryWrapper<BizDeliveryNoticeD>().eq("org_id", ORG_ID).eq("notice_id", nh.getId()).eq("del_flag", 0));
        BigDecimal noticeOutboundTotal = nLines.stream().map(x -> safe(x.getOutboundQty())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal noticeTotal = nLines.stream().map(x -> safe(x.getNoticeQty())).reduce(BigDecimal.ZERO, BigDecimal::add);
        nh.setOutboundQty(noticeOutboundTotal);
        nh.setStatus(noticeOutboundTotal.compareTo(noticeTotal) >= 0 ? "DONE" : "PARTIAL");
        noticeHMapper.updateById(nh);

        // 回写订单头状态
        BizSalesOrderH oh = mustOrder(nh.getOrderId());
        List<BizSalesOrderD> oLines = orderDMapper.selectList(new QueryWrapper<BizSalesOrderD>().eq("org_id", ORG_ID).eq("order_id", oh.getId()).eq("del_flag", 0));
        BigDecimal orderDelivered = oLines.stream().map(x -> safe(x.getDeliveredQty())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal orderTotal = oLines.stream().map(x -> safe(x.getOrderQty())).reduce(BigDecimal.ZERO, BigDecimal::add);
        oh.setDeliveredQty(orderDelivered);
        if ("CLOSED".equals(oh.getStatus()) || "VOID".equals(oh.getStatus())) {
            // 保持人工关闭/作废状态
        } else {
            oh.setStatus(orderDelivered.compareTo(orderTotal) >= 0 ? "DONE" : (orderDelivered.compareTo(BigDecimal.ZERO) > 0 ? "PARTIAL" : "APPROVED"));
        }
        orderHMapper.updateById(oh);

        h.setStatus("APPROVED");
        if (h.getPdaRecordJson() == null || h.getPdaRecordJson().isBlank()) {
            h.setPdaRecordJson("{\"device\":\"PDA-MOCK\",\"scanCount\":" + outLines.size() + "}");
        }
        outboundHMapper.updateById(h);

        // 库存扣减占位
        inventoryStubService.deductForSalesOutbound(h.getId());

        return h;
    }

    public String pdaRecords(Long id) {
        BizSalesOutboundH h = mustOutbound(id);
        return Optional.ofNullable(h.getPdaRecordJson()).orElse("{}");
    }

    // ------------------ 销售退货（骨架） ------------------
    public PageResult<BizSalesReturnH> pageSalesReturns(int pageNo, int pageSize, String keyword) {
        List<BizSalesReturnH> all = returnHMapper.selectList(new QueryWrapper<BizSalesReturnH>().eq("org_id", ORG_ID).eq("del_flag", 0).orderByDesc("id"));
        if (keyword != null && !keyword.isBlank()) all = all.stream().filter(x -> (x.getReturnNo() + "").contains(keyword)).toList();
        return page(all, pageNo, pageSize);
    }

    public Map<String, Object> salesReturnDetail(Long id) {
        BizSalesReturnH h = mustReturn(id);
        List<BizSalesReturnD> lines = returnDMapper.selectList(new QueryWrapper<BizSalesReturnD>().eq("org_id", ORG_ID).eq("return_id", id).eq("del_flag", 0).orderByAsc("line_no"));
        return Map.of("header", h, "lines", lines);
    }

    @Transactional
    public Map<String, Object> saveSalesReturn(SalesDtos.SalesReturnSaveReq req) {
        BizSalesReturnH h = req.getHeader();
        List<BizSalesReturnD> lines = Optional.ofNullable(req.getLines()).orElse(List.of());
        if (h == null || lines.isEmpty()) throw new BizException(400, "退货头/行不能为空");

        if (h.getId() == null) {
            h.setOrgId(ORG_ID);
            h.setReturnNo(Optional.ofNullable(h.getReturnNo()).filter(s -> !s.isBlank()).orElse(noGenerator.next("SR")));
            h.setBizDate(Optional.ofNullable(h.getBizDate()).orElse(LocalDate.now()));
            h.setStatus("DRAFT");
            h.setApprovalStatus("DRAFT");
            h.setTotalQty(sumReturnQty(lines));
            h.setDelFlag(0);
            returnHMapper.insert(h);
        } else {
            BizSalesReturnH old = mustReturn(h.getId());
            old.setCustomerId(h.getCustomerId());
            old.setRemark(h.getRemark());
            old.setTotalQty(sumReturnQty(lines));
            returnHMapper.updateById(old);
            h = old;
            returnDMapper.delete(new QueryWrapper<BizSalesReturnD>().eq("org_id", ORG_ID).eq("return_id", h.getId()));
        }

        int idx = 1;
        for (BizSalesReturnD line : lines) {
            line.setId(null);
            line.setOrgId(ORG_ID);
            line.setReturnId(h.getId());
            line.setLineNo(idx++);
            line.setDelFlag(0);
            if (line.getReturnQty() == null || line.getReturnQty().compareTo(BigDecimal.ZERO) <= 0) throw new BizException(400, "退货数量必须大于0");
            returnDMapper.insert(line);
        }
        return salesReturnDetail(h.getId());
    }

    public BizSalesReturnH auditSalesReturn(Long id, SalesDtos.AuditReq req) {
        BizSalesReturnH h = mustReturn(id);
        boolean ok = Boolean.TRUE.equals(req.getApproved());
        h.setApprovalStatus(ok ? "APPROVED" : "REJECTED");
        h.setStatus(ok ? "APPROVED" : "REJECTED");
        returnHMapper.updateById(h);
        return h;
    }

    // ------------------ 订单执行汇总 ------------------
    public PageResult<SalesDtos.OrderExecutionSummary> orderExecutionSummary(int pageNo, int pageSize, String keyword) {
        List<BizSalesOrderH> all = orderHMapper.selectList(new QueryWrapper<BizSalesOrderH>().eq("org_id", ORG_ID).eq("del_flag", 0).orderByDesc("id"));
        if (keyword != null && !keyword.isBlank()) all = all.stream().filter(x -> (x.getOrderNo() + "").contains(keyword)).toList();

        List<SalesDtos.OrderExecutionSummary> rows = all.stream().map(h -> {
            SalesDtos.OrderExecutionSummary s = new SalesDtos.OrderExecutionSummary();
            s.setOrderId(h.getId());
            s.setOrderNo(h.getOrderNo());
            s.setOrderTotalQty(safe(h.getTotalQty()));
            s.setDeliveredQty(safe(h.getDeliveredQty()));
            s.setUndeliveredQty(safe(h.getTotalQty()).subtract(safe(h.getDeliveredQty())));
            s.setOrderStatus(h.getStatus());
            return s;
        }).toList();

        return page(rows, pageNo, pageSize);
    }

    // ------------------ helpers ------------------
    private BizSalesOrderH mustOrder(Long id) {
        BizSalesOrderH x = orderHMapper.selectById(id);
        if (x == null || x.getDelFlag() != 0) throw new BizException(404, "销售订单不存在");
        return x;
    }

    private BizDeliveryNoticeH mustNotice(Long id) {
        BizDeliveryNoticeH x = noticeHMapper.selectById(id);
        if (x == null || x.getDelFlag() != 0) throw new BizException(404, "发货通知不存在");
        return x;
    }

    private BizSalesOutboundH mustOutbound(Long id) {
        BizSalesOutboundH x = outboundHMapper.selectById(id);
        if (x == null || x.getDelFlag() != 0) throw new BizException(404, "销售出库不存在");
        return x;
    }

    private BizSalesReturnH mustReturn(Long id) {
        BizSalesReturnH x = returnHMapper.selectById(id);
        if (x == null || x.getDelFlag() != 0) throw new BizException(404, "销售退货不存在");
        return x;
    }

    private BigDecimal sumOrderQty(List<BizSalesOrderD> lines) { return lines.stream().map(x -> safe(x.getOrderQty())).reduce(BigDecimal.ZERO, BigDecimal::add); }
    private BigDecimal sumNoticeQty(List<BizDeliveryNoticeD> lines) { return lines.stream().map(x -> safe(x.getNoticeQty())).reduce(BigDecimal.ZERO, BigDecimal::add); }
    private BigDecimal sumOutboundQty(List<BizSalesOutboundD> lines) { return lines.stream().map(x -> safe(x.getOutboundQty())).reduce(BigDecimal.ZERO, BigDecimal::add); }
    private BigDecimal sumReturnQty(List<BizSalesReturnD> lines) { return lines.stream().map(x -> safe(x.getReturnQty())).reduce(BigDecimal.ZERO, BigDecimal::add); }

    private BigDecimal safe(BigDecimal x) { return x == null ? BigDecimal.ZERO : x; }

    private <T> PageResult<T> page(List<T> all, int pageNo, int pageSize) {
        int from = Math.max(0, (pageNo - 1) * pageSize);
        int to = Math.min(all.size(), from + pageSize);
        List<T> data = from >= all.size() ? List.of() : all.subList(from, to);
        return PageResult.of(all.size(), pageNo, pageSize, data);
    }
}
