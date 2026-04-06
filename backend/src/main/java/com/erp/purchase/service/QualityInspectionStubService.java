package com.erp.purchase.service;

import org.springframework.stereotype.Service;

@Service
public class QualityInspectionStubService {
    public boolean canInbound(Long inboundId, boolean qcRequired, boolean qcPassed) {
        if (!qcRequired) return true;
        // 预留来料检验挂点：后续接入IQC流程
        return qcPassed;
    }
}
