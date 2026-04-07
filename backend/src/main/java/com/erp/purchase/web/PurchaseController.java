package com.erp.purchase.web;

import com.erp.common.dto.PageResult;
import com.erp.common.dto.Result;
import com.erp.purchase.dto.PurchaseDtos;
import com.erp.purchase.entity.*;
import com.erp.purchase.service.PurchaseService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {

    private final PurchaseService service;

    public PurchaseController(PurchaseService service) {
        this.service = service;
    }

    // ---------- 采购申请 ----------
    @GetMapping("/reqs")
    public Result<PageResult<BizPurchaseReqH>> reqList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                       @RequestParam(defaultValue = "20") Integer pageSize,
                                                       @RequestParam(required = false) String keyword) {
        return Result.ok(service.pageReqs(pageNo, pageSize, keyword));
    }

    @GetMapping("/reqs/{id}")
    public Result<Map<String, Object>> reqDetail(@PathVariable Long id) { return Result.ok(service.reqDetail(id)); }

    @PostMapping("/reqs")
    public Result<Map<String, Object>> reqSave(@RequestBody PurchaseDtos.PurchaseReqSaveReq req) { return Result.ok(service.saveReq(req)); }

    @PostMapping("/reqs/{id}/submit")
    public Result<BizPurchaseReqH> reqSubmit(@PathVariable Long id) { return Result.ok(service.submitReq(id)); }

    @PostMapping("/reqs/{id}/audit")
    public Result<BizPurchaseReqH> reqAudit(@PathVariable Long id, @RequestBody PurchaseDtos.AuditReq req) { return Result.ok(service.auditReq(id, req)); }

    @PostMapping("/reqs/push-order")
    public Result<Map<String, Object>> reqPushOrder(@RequestBody PurchaseDtos.PushOrderReq req) { return Result.ok(service.pushToOrder(req)); }

    // ---------- 采购订单 ----------
    @GetMapping("/orders")
    public Result<PageResult<BizPurchaseOrderH>> orderList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                           @RequestParam(defaultValue = "20") Integer pageSize,
                                                           @RequestParam(required = false) String keyword) {
        return Result.ok(service.pageOrders(pageNo, pageSize, keyword));
    }

    @GetMapping("/orders/{id}")
    public Result<Map<String, Object>> orderDetail(@PathVariable Long id) { return Result.ok(service.orderDetail(id)); }

    @PostMapping("/orders")
    public Result<Map<String, Object>> orderSave(@RequestBody PurchaseDtos.PurchaseOrderSaveReq req) { return Result.ok(service.saveOrder(req)); }

    @PostMapping("/orders/{id}/audit")
    public Result<BizPurchaseOrderH> orderAudit(@PathVariable Long id, @RequestBody PurchaseDtos.AuditReq req) { return Result.ok(service.auditOrder(id, req)); }

    @PostMapping("/orders/{id}/close")
    public Result<BizPurchaseOrderH> orderClose(@PathVariable Long id) { return Result.ok(service.closeOrder(id)); }

    // ---------- 收货 ----------
    @GetMapping("/receipts")
    public Result<PageResult<BizPurchaseReceiptH>> receiptList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                               @RequestParam(defaultValue = "20") Integer pageSize,
                                                               @RequestParam(required = false) String keyword) {
        return Result.ok(service.pageReceipts(pageNo, pageSize, keyword));
    }

    @GetMapping("/receipts/{id}")
    public Result<Map<String, Object>> receiptDetail(@PathVariable Long id) { return Result.ok(service.receiptDetail(id)); }

    @PostMapping("/receipts/confirm")
    public Result<Map<String, Object>> receiptConfirm(@RequestBody PurchaseDtos.ReceiptConfirmReq req) { return Result.ok(service.confirmReceipt(req)); }

    // ---------- 采购入库 ----------
    @GetMapping("/inbounds")
    public Result<PageResult<BizPurchaseInboundH>> inboundList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                               @RequestParam(defaultValue = "20") Integer pageSize,
                                                               @RequestParam(required = false) String keyword) {
        return Result.ok(service.pageInbounds(pageNo, pageSize, keyword));
    }

    @GetMapping("/inbounds/{id}")
    public Result<Map<String, Object>> inboundDetail(@PathVariable Long id) { return Result.ok(service.inboundDetail(id)); }

    @PostMapping("/inbounds")
    public Result<Map<String, Object>> inboundSave(@RequestBody PurchaseDtos.InboundSaveReq req) { return Result.ok(service.saveInbound(req)); }

    @PostMapping("/inbounds/{id}/audit")
    public Result<BizPurchaseInboundH> inboundAudit(@PathVariable Long id, @RequestBody PurchaseDtos.AuditReq req) { return Result.ok(service.auditInbound(id, req)); }

    @PostMapping("/inbounds/{id}/unaudit")
    public Result<BizPurchaseInboundH> inboundUnAudit(@PathVariable Long id) { return Result.ok(service.unAuditInbound(id)); }

    // ---------- 采购退货（骨架） ----------
    @GetMapping("/returns")
    public Result<PageResult<BizPurchaseReturnH>> returnList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                             @RequestParam(defaultValue = "20") Integer pageSize,
                                                             @RequestParam(required = false) String keyword) {
        return Result.ok(service.pageReturns(pageNo, pageSize, keyword));
    }

    @GetMapping("/returns/{id}")
    public Result<Map<String, Object>> returnDetail(@PathVariable Long id) { return Result.ok(service.returnDetail(id)); }

    @PostMapping("/returns")
    public Result<Map<String, Object>> returnSave(@RequestBody PurchaseDtos.PurchaseReturnSaveReq req) { return Result.ok(service.saveReturn(req)); }

    @PostMapping("/returns/{id}/audit")
    public Result<BizPurchaseReturnH> returnAudit(@PathVariable Long id, @RequestBody PurchaseDtos.AuditReq req) { return Result.ok(service.auditReturn(id, req)); }
}
