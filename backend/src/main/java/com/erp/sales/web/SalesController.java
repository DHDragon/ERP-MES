package com.erp.sales.web;

import com.erp.common.dto.PageResult;
import com.erp.common.dto.Result;
import com.erp.sales.dto.SalesDtos;
import com.erp.sales.entity.*;
import com.erp.sales.service.SalesService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sales")
public class SalesController {

    private final SalesService service;

    public SalesController(SalesService service) {
        this.service = service;
    }

    // ------------------ 销售订单 ------------------
    @GetMapping("/orders")
    public Result<PageResult<BizSalesOrderH>> orderList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                        @RequestParam(defaultValue = "20") Integer pageSize,
                                                        @RequestParam(required = false) String keyword) {
        return Result.ok(service.pageSalesOrders(pageNo, pageSize, keyword));
    }

    @GetMapping("/orders/{id}")
    public Result<Map<String, Object>> orderDetail(@PathVariable Long id) { return Result.ok(service.salesOrderDetail(id)); }

    @PostMapping("/orders")
    public Result<Map<String, Object>> orderSave(@RequestBody SalesDtos.SalesOrderSaveReq req) { return Result.ok(service.saveSalesOrder(req)); }

    @PostMapping("/orders/{id}/submit")
    public Result<BizSalesOrderH> orderSubmit(@PathVariable Long id) { return Result.ok(service.submitSalesOrder(id)); }

    @PostMapping("/orders/{id}/audit")
    public Result<BizSalesOrderH> orderAudit(@PathVariable Long id, @RequestBody SalesDtos.AuditReq req) { return Result.ok(service.auditSalesOrder(id, req)); }

    @PostMapping("/orders/{id}/unaudit")
    public Result<BizSalesOrderH> orderUnAudit(@PathVariable Long id) { return Result.ok(service.unAuditSalesOrder(id)); }

    @PostMapping("/orders/{id}/close")
    public Result<BizSalesOrderH> orderClose(@PathVariable Long id) { return Result.ok(service.closeSalesOrder(id)); }

    @PostMapping("/orders/{id}/void")
    public Result<BizSalesOrderH> orderVoid(@PathVariable Long id) { return Result.ok(service.voidSalesOrder(id)); }

    @PostMapping("/orders/push-delivery-notice")
    public Result<Map<String, Object>> orderPushNotice(@RequestBody SalesDtos.PushNoticeReq req) { return Result.ok(service.pushToDeliveryNotice(req)); }

    // ------------------ 发货通知 ------------------
    @GetMapping("/delivery-notices")
    public Result<PageResult<BizDeliveryNoticeH>> noticeList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                             @RequestParam(defaultValue = "20") Integer pageSize,
                                                             @RequestParam(required = false) String keyword) {
        return Result.ok(service.pageDeliveryNotices(pageNo, pageSize, keyword));
    }

    @GetMapping("/delivery-notices/{id}")
    public Result<Map<String, Object>> noticeDetail(@PathVariable Long id) { return Result.ok(service.deliveryNoticeDetail(id)); }

    @PostMapping("/delivery-notices")
    public Result<Map<String, Object>> noticeSave(@RequestBody SalesDtos.DeliveryNoticeSaveReq req) { return Result.ok(service.saveDeliveryNotice(req)); }

    @PostMapping("/delivery-notices/{id}/audit")
    public Result<BizDeliveryNoticeH> noticeAudit(@PathVariable Long id) { return Result.ok(service.auditDeliveryNotice(id)); }

    @PostMapping("/delivery-notices/{id}/close")
    public Result<BizDeliveryNoticeH> noticeClose(@PathVariable Long id) { return Result.ok(service.closeDeliveryNotice(id)); }

    // ------------------ 销售出库 ------------------
    @GetMapping("/outbounds")
    public Result<PageResult<BizSalesOutboundH>> outboundList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                              @RequestParam(defaultValue = "20") Integer pageSize,
                                                              @RequestParam(required = false) String keyword) {
        return Result.ok(service.pageSalesOutbounds(pageNo, pageSize, keyword));
    }

    @GetMapping("/outbounds/{id}")
    public Result<Map<String, Object>> outboundDetail(@PathVariable Long id) { return Result.ok(service.salesOutboundDetail(id)); }

    @PostMapping("/outbounds")
    public Result<Map<String, Object>> outboundSave(@RequestBody SalesDtos.SalesOutboundSaveReq req) { return Result.ok(service.saveSalesOutbound(req)); }

    @PostMapping("/outbounds/{id}/audit")
    public Result<BizSalesOutboundH> outboundAudit(@PathVariable Long id) { return Result.ok(service.auditSalesOutbound(id)); }

    @PostMapping("/outbounds/{id}/unaudit")
    public Result<BizSalesOutboundH> outboundUnAudit(@PathVariable Long id) { return Result.ok(service.unAuditSalesOutbound(id)); }

    @GetMapping("/outbounds/{id}/pda-records")
    public Result<String> outboundPda(@PathVariable Long id) { return Result.ok(service.pdaRecords(id)); }

    // ------------------ 销售退货（骨架） ------------------
    @GetMapping("/returns")
    public Result<PageResult<BizSalesReturnH>> returnList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                          @RequestParam(defaultValue = "20") Integer pageSize,
                                                          @RequestParam(required = false) String keyword) {
        return Result.ok(service.pageSalesReturns(pageNo, pageSize, keyword));
    }

    @GetMapping("/returns/{id}")
    public Result<Map<String, Object>> returnDetail(@PathVariable Long id) { return Result.ok(service.salesReturnDetail(id)); }

    @PostMapping("/returns")
    public Result<Map<String, Object>> returnSave(@RequestBody SalesDtos.SalesReturnSaveReq req) { return Result.ok(service.saveSalesReturn(req)); }

    @PostMapping("/returns/{id}/audit")
    public Result<BizSalesReturnH> returnAudit(@PathVariable Long id, @RequestBody SalesDtos.AuditReq req) { return Result.ok(service.auditSalesReturn(id, req)); }

    // ------------------ 订单执行汇总 ------------------
    @GetMapping("/order-executions")
    public Result<PageResult<SalesDtos.OrderExecutionSummary>> orderExecutionSummary(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                     @RequestParam(defaultValue = "20") Integer pageSize,
                                                                                     @RequestParam(required = false) String keyword) {
        return Result.ok(service.orderExecutionSummary(pageNo, pageSize, keyword));
    }
}
