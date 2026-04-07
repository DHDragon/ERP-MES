package com.erp.purchase.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.erp.common.exception.BizException;
import com.erp.purchase.dto.PurchaseDtos;
import com.erp.purchase.entity.*;
import com.erp.purchase.mapper.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceLinkageTest {

    @Mock BizPurchaseReqHMapper reqHMapper;
    @Mock BizPurchaseReqDMapper reqDMapper;
    @Mock BizPurchaseOrderHMapper orderHMapper;
    @Mock BizPurchaseOrderDMapper orderDMapper;
    @Mock BizPurchaseReceiptHMapper receiptHMapper;
    @Mock BizPurchaseReceiptDMapper receiptDMapper;
    @Mock BizPurchaseInboundHMapper inboundHMapper;
    @Mock BizPurchaseInboundDMapper inboundDMapper;
    @Mock BizPurchaseReturnHMapper returnHMapper;
    @Mock BizPurchaseReturnDMapper returnDMapper;
    @Mock PurchaseNoGenerator noGenerator;
    @Mock QualityInspectionStubService qualityInspectionStubService;
    @Mock InventoryInboundStubService inventoryInboundStubService;

    private PurchaseService service;

    @BeforeEach
    void setUp() {
        service = new PurchaseService(reqHMapper, reqDMapper, orderHMapper, orderDMapper,
                receiptHMapper, receiptDMapper, inboundHMapper, inboundDMapper,
                returnHMapper, returnDMapper, noGenerator, qualityInspectionStubService,
                inventoryInboundStubService);
    }

    @Test
    void purchaseInboundAudit_shouldIncreaseInventory_andKeepWriteBackConsistent() {
        BizPurchaseInboundH inboundH = new BizPurchaseInboundH();
        inboundH.setId(300L);
        inboundH.setReceiptId(30L);
        inboundH.setQcRequired(0);
        inboundH.setQcPassed(0);
        inboundH.setStatus("DRAFT");
        inboundH.setDelFlag(0);

        BizPurchaseInboundD inboundD = new BizPurchaseInboundD();
        inboundD.setId(3001L);
        inboundD.setReceiptLineId(30001L);
        inboundD.setInboundQty(new BigDecimal("4"));

        BizPurchaseReceiptD receiptD = new BizPurchaseReceiptD();
        receiptD.setId(30001L);
        receiptD.setOrderLineId(40001L);
        receiptD.setReceiptQty(new BigDecimal("10"));
        receiptD.setInboundQty(new BigDecimal("1"));
        receiptD.setDelFlag(0);

        BizPurchaseOrderD orderD = new BizPurchaseOrderD();
        orderD.setId(40001L);
        orderD.setOrderQty(new BigDecimal("20"));
        orderD.setInboundQty(new BigDecimal("6"));
        orderD.setDelFlag(0);

        BizPurchaseReceiptH receiptH = new BizPurchaseReceiptH();
        receiptH.setId(30L);
        receiptH.setOrderId(40L);
        receiptH.setDelFlag(0);

        BizPurchaseOrderH orderH = new BizPurchaseOrderH();
        orderH.setId(40L);
        orderH.setStatus("APPROVED");
        orderH.setDelFlag(0);

        when(inboundHMapper.selectById(300L)).thenReturn(inboundH);
        when(qualityInspectionStubService.canInbound(300L, false, false)).thenReturn(true);
        when(inboundDMapper.selectList(any(QueryWrapper.class))).thenReturn(List.of(inboundD));
        when(receiptDMapper.selectById(30001L)).thenReturn(receiptD);
        when(orderDMapper.selectById(40001L)).thenReturn(orderD);
        when(receiptHMapper.selectById(30L)).thenReturn(receiptH);
        when(receiptDMapper.selectList(any(QueryWrapper.class))).thenReturn(List.of(receiptD));
        when(orderHMapper.selectById(40L)).thenReturn(orderH);
        when(orderDMapper.selectList(any(QueryWrapper.class))).thenReturn(List.of(orderD));

        PurchaseDtos.AuditReq req = new PurchaseDtos.AuditReq();
        req.setApproved(true);

        BizPurchaseInboundH result = service.auditInbound(300L, req);

        assertEquals("APPROVED", result.getStatus());
        assertEquals(new BigDecimal("5"), receiptD.getInboundQty());
        assertEquals(new BigDecimal("10"), orderD.getInboundQty());
        assertEquals(new BigDecimal("5"), receiptH.getInboundQty());
        assertEquals(new BigDecimal("10"), orderH.getInboundQty());
        verify(inventoryInboundStubService).writeLedgerForPurchaseInbound(300L);
    }

    @Test
    void purchaseInboundUnAudit_shouldRollbackInventory_andRestoreQuantities() {
        BizPurchaseInboundH inboundH = new BizPurchaseInboundH();
        inboundH.setId(300L);
        inboundH.setReceiptId(30L);
        inboundH.setQcRequired(0);
        inboundH.setQcPassed(0);
        inboundH.setStatus("APPROVED");
        inboundH.setDelFlag(0);

        BizPurchaseInboundD inboundD = new BizPurchaseInboundD();
        inboundD.setId(3001L);
        inboundD.setReceiptLineId(30001L);
        inboundD.setInboundQty(new BigDecimal("4"));

        BizPurchaseReceiptD receiptD = new BizPurchaseReceiptD();
        receiptD.setId(30001L);
        receiptD.setOrderLineId(40001L);
        receiptD.setReceiptQty(new BigDecimal("10"));
        receiptD.setInboundQty(new BigDecimal("5"));
        receiptD.setDelFlag(0);

        BizPurchaseOrderD orderD = new BizPurchaseOrderD();
        orderD.setId(40001L);
        orderD.setOrderQty(new BigDecimal("20"));
        orderD.setInboundQty(new BigDecimal("10"));
        orderD.setDelFlag(0);

        BizPurchaseReceiptH receiptH = new BizPurchaseReceiptH();
        receiptH.setId(30L);
        receiptH.setOrderId(40L);
        receiptH.setDelFlag(0);

        BizPurchaseOrderH orderH = new BizPurchaseOrderH();
        orderH.setId(40L);
        orderH.setStatus("PARTIAL");
        orderH.setDelFlag(0);

        when(inboundHMapper.selectById(300L)).thenReturn(inboundH);
        when(inboundDMapper.selectList(any(QueryWrapper.class))).thenReturn(List.of(inboundD));
        when(receiptDMapper.selectById(30001L)).thenReturn(receiptD);
        when(orderDMapper.selectById(40001L)).thenReturn(orderD);
        when(receiptHMapper.selectById(30L)).thenReturn(receiptH);
        when(receiptDMapper.selectList(any(QueryWrapper.class))).thenReturn(List.of(receiptD));
        when(orderHMapper.selectById(40L)).thenReturn(orderH);
        when(orderDMapper.selectList(any(QueryWrapper.class))).thenReturn(List.of(orderD));

        BizPurchaseInboundH result = service.unAuditInbound(300L);

        assertEquals("DRAFT", result.getStatus());
        assertEquals(new BigDecimal("1"), receiptD.getInboundQty());
        assertEquals(new BigDecimal("6"), orderD.getInboundQty());
        assertEquals(new BigDecimal("1"), receiptH.getInboundQty());
        assertEquals(new BigDecimal("6"), orderH.getInboundQty());
        verify(inventoryInboundStubService).rollbackForPurchaseInbound(300L);
    }

    @Test
    void purchaseInboundAudit_shouldRejectWhenQcRequiredButNotPassed() {
        BizPurchaseInboundH inboundH = new BizPurchaseInboundH();
        inboundH.setId(301L);
        inboundH.setQcRequired(1);
        inboundH.setQcPassed(0);
        inboundH.setDelFlag(0);

        when(inboundHMapper.selectById(301L)).thenReturn(inboundH);
        when(qualityInspectionStubService.canInbound(301L, true, false)).thenReturn(false);

        PurchaseDtos.AuditReq req = new PurchaseDtos.AuditReq();
        req.setApproved(true);

        assertThrows(BizException.class, () -> service.auditInbound(301L, req));
        verify(inventoryInboundStubService, never()).writeLedgerForPurchaseInbound(anyLong());
    }
}
