package com.erp.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.erp.system.entity.SysMenu;
import com.erp.system.mapper.SysMenuMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysMenuService {

    private final SysMenuMapper mapper;

    public SysMenuService(SysMenuMapper mapper) {
        this.mapper = mapper;
    }

    public List<SysMenu> listEnabledMenus(long orgId) {
        QueryWrapper<SysMenu> qw = new QueryWrapper<>();
        qw.eq("org_id", orgId)
          .eq("del_flag", 0)
          .eq("status", "ENABLED")
          .orderByAsc("sort_no");
        return mapper.selectList(qw);
    }
}
