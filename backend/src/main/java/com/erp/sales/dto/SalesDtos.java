package com.erp.sales.dto;

import com.erp.sales.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class SalesDtos {

    public static class AuditReq {
        private Boolean approved;
        private String remark;

        public Boolean getApproved() { return approved; }
        public void setApproved(Boolean approved) { this.approved = approved; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
    }

    public static class SalesOrderSaveReq {
        private BizSalesOrderH header;
        private List<BizSalesOrderD> lines;

        public BizSalesOrderH getHeader() { return header; }
        public void setHeader(BizSalesOrderH header) { this.header = header; }
        public List<BizSalesOrderD> getLines() { return lines; }
        public void setLines(List<BizSalesOrderD> lines) { this.lines = lines; }
    }

    public static class DeliveryNoticeSaveReq {
        private BizDeliveryNoticeH header;
        private List<BizDeliveryNoticeD> lines;

        public BizDeliveryNoticeH getHeader() { return header; }
        public void setHeader(BizDeliveryNoticeH header) { this.header = header; }
        public List<BizDeliveryNoticeD> getLines() { return lines; }
        public void setLines(List<BizDeliveryNoticeD> lines) { this.lines = lines; }
    }

    public static class SalesOutboundSaveReq {
        private BizSalesOutboundH header;
        private List<BizSalesOutboundD> lines;

        public BizSalesOutboundH getHeader() { return header; }
        public void setHeader(BizSalesOutboundH header) { this.header = header; }
        public List<BizSalesOutboundD> getLines() { return lines; }
        public void setLines(List<BizSalesOutboundD> lines) { this.lines = lines; }
    }

    public static class SalesReturnSaveReq {
        private BizSalesReturnH header;
        private List<BizSalesReturnD> lines;

        public BizSalesReturnH getHeader() { return header; }
        public void setHeader(BizSalesReturnH header) { this.header = header; }
        public List<BizSalesReturnD> getLines() { return lines; }
        public void setLines(List<BizSalesReturnD> lines) { this.lines = lines; }
    }

    public static class PushNoticeReq {
        private Long orderId;
        private LocalDate bizDate;
        private List<PushLine> lines;

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public LocalDate getBizDate() { return bizDate; }
        public void setBizDate(LocalDate bizDate) { this.bizDate = bizDate; }
        public List<PushLine> getLines() { return lines; }
        public void setLines(List<PushLine> lines) { this.lines = lines; }
    }

    public static class PushLine {
        private Long sourceLineId;
        private BigDecimal qty;

        public Long getSourceLineId() { return sourceLineId; }
        public void setSourceLineId(Long sourceLineId) { this.sourceLineId = sourceLineId; }
        public BigDecimal getQty() { return qty; }
        public void setQty(BigDecimal qty) { this.qty = qty; }
    }

    public static class OrderExecutionSummary {
        private Long orderId;
        private String orderNo;
        private BigDecimal orderTotalQty;
        private BigDecimal deliveredQty;
        private BigDecimal undeliveredQty;
        private String orderStatus;

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public String getOrderNo() { return orderNo; }
        public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
        public BigDecimal getOrderTotalQty() { return orderTotalQty; }
        public void setOrderTotalQty(BigDecimal orderTotalQty) { this.orderTotalQty = orderTotalQty; }
        public BigDecimal getDeliveredQty() { return deliveredQty; }
        public void setDeliveredQty(BigDecimal deliveredQty) { this.deliveredQty = deliveredQty; }
        public BigDecimal getUndeliveredQty() { return undeliveredQty; }
        public void setUndeliveredQty(BigDecimal undeliveredQty) { this.undeliveredQty = undeliveredQty; }
        public String getOrderStatus() { return orderStatus; }
        public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    }
}
