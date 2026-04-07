package com.erp.sales.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.erp.common.dto.PageResult;
import com.erp.sales.dto.SalesDtos;
import com.erp.sales.entity.*;
import com.erp.sales.mapper.*;
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
class SalesServiceLinkageTest {

    @Mock BizSalesOrderHMapper orderHMapper;
    @Mock BizSalesOrderDMapper orderDMapper;
    @Mock BizDeliveryNoticeHMapper noticeHMapper;
    @Mock BizDeliveryNoticeDMapper noticeDMapper;
    @Mock BizSalesOutboundHMapper outboundHMapper;
    @Mock BizSalesOutboundDMapper outboundDMapper;
    @Mock BizSalesReturnHMapper returnHMapper;
    @Mock BizSalesReturnDMapper returnDMapper;
    @Mock InventoryStubService inventoryStubService;
    @Mock SalesNoGenerator noGenerator;

    private SalesService service;

    @BeforeEach
    void setUp() {
        service = new SalesService(orderHMapper, orderDMapper, noticeHMapper, noticeDMapper,
                outboundHMapper, outboundDMapper, returnHMapper, returnDMapper,
                inventoryStubService, noGenerator);
    }

    @Test
    void salesOutboundAudit_shouldDeductInventory_andKeepWriteBackConsistent() {
        BizSalesOutboundH outboundH = new BizSalesOutboundH();
        outboundH.setId(100L);
        outboundH.setNoticeId(10L);
        outboundH.setStatus("DRAFT");
        outboundH.setDelFlag(0);

        BizSalesOutboundD outboundD = new BizSalesOutboundD();
        outboundD.setId(1001L);
        outboundD.setNoticeLineId(10001L);
        outboundD.setOutboundQty(new BigDecimal("5"));

        BizDeliveryNoticeD noticeD = new BizDeliveryNoticeD();
        noticeD.setId(10001L);
        noticeD.setNoticeQty(new BigDecimal("10"));
        noticeD.setOutboundQty(new BigDecimal("2"));
        noticeD.setOrderLineId(20001L);
        noticeD.setDelFlag(0);

        BizSalesOrderD orderD = new BizSalesOrderD();
        orderD.setId(20001L);
        orderD.setOrderQty(new BigDecimal("20"));
        orderD.setDeliveredQty(new BigDecimal("3"));
        orderD.setDelFlag(0);

        BizDeliveryNoticeH noticeH = new BizDeliveryNoticeH();
        noticeH.setId(10L);
        noticeH.setOrderId(20L);
        noticeH.setDelFlag(0);

        BizSalesOrderH orderH = new BizSalesOrderH();
        orderH.setId(20L);
        orderH.setStatus("APPROVED");
        orderH.setDelFlag(0);

        when(outboundHMapper.selectById(100L)).thenReturn(outboundH);
        when(outboundDMapper.selectList(any(QueryWrapper.class))).thenReturn(List.of(outboundD));
        when(noticeDMapper.selectById(10001L)).thenReturn(noticeD);
        when(orderDMapper.selectById(20001L)).thenReturn(orderD);
        when(noticeHMapper.selectById(10L)).thenReturn(noticeH);
        when(noticeDMapper.selectList(any(QueryWrapper.class))).thenReturn(List.of(noticeD));
        when(orderHMapper.selectById(20L)).thenReturn(orderH);
        when(orderDMapper.selectList(any(QueryWrapper.class))).thenReturn(List.of(orderD));

        BizSalesOutboundH result = service.auditSalesOutbound(100L);

        assertEquals("APPROVED", result.getStatus());
        assertEquals(new BigDecimal("7"), noticeD.getOutboundQty());
        assertEquals(new BigDecimal("8"), orderD.getDeliveredQty());
        assertEquals(new BigDecimal("7"), noticeH.getOutboundQty());
        assertEquals(new BigDecimal("8"), orderH.getDeliveredQty());
        verify(inventoryStubService).deductForSalesOutbound(100L);
    }

    @Test
    void salesOutboundUnAudit_shouldRollbackInventory_andRestoreQuantities() {
        BizSalesOutboundH outboundH = new BizSalesOutboundH();
        outboundH.setId(100L);
        outboundH.setNoticeId(10L);
        outboundH.setStatus("APPROVED");
        outboundH.setDelFlag(0);

        BizSalesOutboundD outboundD = new BizSalesOutboundD();
        outboundD.setId(1001L);
        outboundD.setNoticeLineId(10001L);
        outboundD.setOutboundQty(new BigDecimal("5"));

        BizDeliveryNoticeD noticeD = new BizDeliveryNoticeD();
        noticeD.setId(10001L);
        noticeD.setNoticeQty(new BigDecimal("10"));
        noticeD.setOutboundQty(new BigDecimal("7"));
        noticeD.setOrderLineId(20001L);
        noticeD.setDelFlag(0);

        BizSalesOrderD orderD = new BizSalesOrderD();
        orderD.setId(20001L);
        orderD.setOrderQty(new BigDecimal("20"));
        orderD.setDeliveredQty(new BigDecimal("8"));
        orderD.setDelFlag(0);

        BizDeliveryNoticeH noticeH = new BizDeliveryNoticeH();
        noticeH.setId(10L);
        noticeH.setOrderId(20L);
        noticeH.setDelFlag(0);

        BizSalesOrderH orderH = new BizSalesOrderH();
        orderH.setId(20L);
        orderH.setStatus("PARTIAL");
        orderH.setDelFlag(0);

        when(outboundHMapper.selectById(100L)).thenReturn(outboundH);
        when(outboundDMapper.selectList(any(QueryWrapper.class))).thenReturn(List.of(outboundD));
        when(noticeDMapper.selectById(10001L)).thenReturn(noticeD);
        when(orderDMapper.selectById(20001L)).thenReturn(orderD);
        when(noticeHMapper.selectById(10L)).thenReturn(noticeH);
        when(noticeDMapper.selectList(any(QueryWrapper.class))).thenReturn(List.of(noticeD));
        when(orderHMapper.selectById(20L)).thenReturn(orderH);
        when(orderDMapper.selectList(any(QueryWrapper.class))).thenReturn(List.of(orderD));

        BizSalesOutboundH result = service.unAuditSalesOutbound(100L);

        assertEquals("DRAFT", result.getStatus());
        assertEquals(new BigDecimal("2"), noticeD.getOutboundQty());
        assertEquals(new BigDecimal("3"), orderD.getDeliveredQty());
        assertEquals(new BigDecimal("2"), noticeH.getOutboundQty());
        assertEquals(new BigDecimal("3"), orderH.getDeliveredQty());
        verify(inventoryStubService).rollbackForSalesOutbound(100L);
    }

    @Test
    void orderExecutionSummary_shouldMatchHeaderStatistics() {
        BizSalesOrderH orderH = new BizSalesOrderH();
        orderH.setId(20L);
        orderH.setOrderNo("SO20260407001");
        orderH.setTotalQty(new BigDecimal("20"));
        orderH.setDeliveredQty(new BigDecimal("8"));
        orderH.setStatus("PARTIAL");
        orderH.setDelFlag(0);

        when(orderHMapper.selectList(any(QueryWrapper.class))).thenReturn(List.of(orderH));

        PageResult<SalesDtos.OrderExecutionSummary> page = service.orderExecutionSummary(1, 20, null);
        assertEquals(1, page.getRecords().size());
        SalesDtos.OrderExecutionSummary row = page.getRecords().get(0);
        assertEquals(new BigDecimal("20"), row.getOrderTotalQty());
        assertEquals(new BigDecimal("8"), row.getDeliveredQty());
        assertEquals(new BigDecimal("12"), row.getUndeliveredQty());
        assertEquals("PARTIAL", row.getOrderStatus());
    }
}
