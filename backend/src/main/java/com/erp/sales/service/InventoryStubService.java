package com.erp.sales.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.erp.common.exception.BizException;
import com.erp.inventory.service.InventoryService;
import com.erp.sales.entity.BizSalesOutboundD;
import com.erp.sales.entity.BizSalesOutboundH;
import com.erp.sales.mapper.BizSalesOutboundDMapper;
import com.erp.sales.mapper.BizSalesOutboundHMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryStubService {

    private static final Logger log = LoggerFactory.getLogger(InventoryStubService.class);
    private static final long ORG_ID = 1L;

    private final BizSalesOutboundHMapper outboundHMapper;
    private final BizSalesOutboundDMapper outboundDMapper;
    private final InventoryService inventoryService;

    public InventoryStubService(BizSalesOutboundHMapper outboundHMapper,
                                BizSalesOutboundDMapper outboundDMapper,
                                InventoryService inventoryService) {
        this.outboundHMapper = outboundHMapper;
        this.outboundDMapper = outboundDMapper;
        this.inventoryService = inventoryService;
    }

    @Transactional
    public void deductForSalesOutbound(Long outboundId) {
        BizSalesOutboundH h = outboundHMapper.selectById(outboundId);
        if (h == null || h.getDelFlag() != 0) throw new BizException(404, "销售出库不存在");

        List<BizSalesOutboundD> lines = outboundDMapper.selectList(new QueryWrapper<BizSalesOutboundD>()
                .eq("org_id", ORG_ID)
                .eq("outbound_id", outboundId)
                .eq("del_flag", 0));

        log.info("[INV-LINK] sales outbound audit start, outboundId={}, lineCount={}", outboundId, lines.size());

        for (BizSalesOutboundD line : lines) {
            inventoryService.outbound("SALES_OUTBOUND", h.getId(), line.getId(),
                    1L, null,
                    line.getMaterialId(),
                    null, null,
                    "AVAILABLE",
                    line.getOutboundQty(),
                    "销售出库审核扣减库存");
            log.info("[INV-LINK] sales outbound line posted, outboundId={}, lineId={}, materialId={}, qty={}",
                    outboundId, line.getId(), line.getMaterialId(), line.getOutboundQty());
        }
    }

    @Transactional
    public int rollbackForSalesOutbound(Long outboundId) {
        BizSalesOutboundH h = outboundHMapper.selectById(outboundId);
        if (h == null || h.getDelFlag() != 0) throw new BizException(404, "销售出库不存在");
        int rolled = inventoryService.rollback("SALES_OUTBOUND", outboundId);
        log.info("[INV-LINK] sales outbound rollback, outboundId={}, rolled={}", outboundId, rolled);
        return rolled;
    }
}
