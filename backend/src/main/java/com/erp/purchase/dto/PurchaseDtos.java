package com.erp.purchase.dto;

import com.erp.purchase.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PurchaseDtos {

    public static class AuditReq {
        private Boolean approved;
        private String remark;

        public Boolean getApproved() { return approved; }
        public void setApproved(Boolean approved) { this.approved = approved; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
    }

    public static class PurchaseReqSaveReq {
        private BizPurchaseReqH header;
        private List<BizPurchaseReqD> lines;

        public BizPurchaseReqH getHeader() { return header; }
        public void setHeader(BizPurchaseReqH header) { this.header = header; }
        public List<BizPurchaseReqD> getLines() { return lines; }
        public void setLines(List<BizPurchaseReqD> lines) { this.lines = lines; }
    }

    public static class PurchaseOrderSaveReq {
        private BizPurchaseOrderH header;
        private List<BizPurchaseOrderD> lines;

        public BizPurchaseOrderH getHeader() { return header; }
        public void setHeader(BizPurchaseOrderH header) { this.header = header; }
        public List<BizPurchaseOrderD> getLines() { return lines; }
        public void setLines(List<BizPurchaseOrderD> lines) { this.lines = lines; }
    }

    public static class PurchaseReturnSaveReq {
        private BizPurchaseReturnH header;
        private List<BizPurchaseReturnD> lines;

        public BizPurchaseReturnH getHeader() { return header; }
        public void setHeader(BizPurchaseReturnH header) { this.header = header; }
        public List<BizPurchaseReturnD> getLines() { return lines; }
        public void setLines(List<BizPurchaseReturnD> lines) { this.lines = lines; }
    }

    public static class PushOrderReq {
        private Long reqId;
        private LocalDate bizDate;
        private List<PushLine> lines;

        public Long getReqId() { return reqId; }
        public void setReqId(Long reqId) { this.reqId = reqId; }
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

    public static class ReceiptConfirmReq {
        private BizPurchaseReceiptH header;
        private List<BizPurchaseReceiptD> lines;

        public BizPurchaseReceiptH getHeader() { return header; }
        public void setHeader(BizPurchaseReceiptH header) { this.header = header; }
        public List<BizPurchaseReceiptD> getLines() { return lines; }
        public void setLines(List<BizPurchaseReceiptD> lines) { this.lines = lines; }
    }

    public static class InboundSaveReq {
        private BizPurchaseInboundH header;
        private List<BizPurchaseInboundD> lines;

        public BizPurchaseInboundH getHeader() { return header; }
        public void setHeader(BizPurchaseInboundH header) { this.header = header; }
        public List<BizPurchaseInboundD> getLines() { return lines; }
        public void setLines(List<BizPurchaseInboundD> lines) { this.lines = lines; }
    }
}
