package com.erp.purchase.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.erp.common.exception.BizException;
import com.erp.inventory.service.InventoryService;
import com.erp.purchase.entity.BizPurchaseInboundD;
import com.erp.purchase.entity.BizPurchaseInboundH;
import com.erp.purchase.mapper.BizPurchaseInboundDMapper;
import com.erp.purchase.mapper.BizPurchaseInboundHMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryInboundStubService {

    private static final Logger log = LoggerFactory.getLogger(InventoryInboundStubService.class);
    private static final long ORG_ID = 1L;

    private final BizPurchaseInboundHMapper inboundHMapper;
    private final BizPurchaseInboundDMapper inboundDMapper;
    private final InventoryService inventoryService;

    public InventoryInboundStubService(BizPurchaseInboundHMapper inboundHMapper,
                                       BizPurchaseInboundDMapper inboundDMapper,
                                       InventoryService inventoryService) {
        this.inboundHMapper = inboundHMapper;
        this.inboundDMapper = inboundDMapper;
        this.inventoryService = inventoryService;
    }

    @Transactional
    public void writeLedgerForPurchaseInbound(Long inboundId) {
        BizPurchaseInboundH h = inboundHMapper.selectById(inboundId);
        if (h == null || h.getDelFlag() != 0) throw new BizException(404, "采购入库不存在");

        List<BizPurchaseInboundD> lines = inboundDMapper.selectList(new QueryWrapper<BizPurchaseInboundD>()
                .eq("org_id", ORG_ID)
                .eq("inbound_id", inboundId)
                .eq("del_flag", 0));

        log.info("[INV-LINK] purchase inbound audit start, inboundId={}, lineCount={}", inboundId, lines.size());

        for (BizPurchaseInboundD line : lines) {
            inventoryService.inbound("PURCHASE_INBOUND", h.getId(), line.getId(),
                    1L, null,
                    line.getMaterialId(),
                    null, null,
                    "AVAILABLE",
                    line.getInboundQty(),
                    "采购入库审核增加库存");
            log.info("[INV-LINK] purchase inbound line posted, inboundId={}, lineId={}, materialId={}, qty={}",
                    inboundId, line.getId(), line.getMaterialId(), line.getInboundQty());
        }
    }

    @Transactional
    public int rollbackForPurchaseInbound(Long inboundId) {
        BizPurchaseInboundH h = inboundHMapper.selectById(inboundId);
        if (h == null || h.getDelFlag() != 0) throw new BizException(404, "采购入库不存在");
        int rolled = inventoryService.rollback("PURCHASE_INBOUND", inboundId);
        log.info("[INV-LINK] purchase inbound rollback, inboundId={}, rolled={}", inboundId, rolled);
        return rolled;
    }
}
