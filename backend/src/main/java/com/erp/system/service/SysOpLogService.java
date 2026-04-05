package com.erp.system.service;

import com.erp.system.entity.SysOpLog;
import com.erp.system.mapper.SysOpLogMapper;
import org.springframework.stereotype.Service;

@Service
public class SysOpLogService {

    private final SysOpLogMapper mapper;

    public SysOpLogService(SysOpLogMapper mapper) {
        this.mapper = mapper;
    }

    public void saveQuietly(SysOpLog logEntity) {
        try {
            mapper.insert(logEntity);
        } catch (Exception ignored) {
            // 日志写入不影响主流程
        }
    }
}
