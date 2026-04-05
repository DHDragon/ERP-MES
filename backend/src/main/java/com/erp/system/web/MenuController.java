package com.erp.system.web;

import com.erp.common.dto.Result;
import com.erp.system.entity.SysMenu;
import com.erp.system.service.SysMenuService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/system")
public class MenuController {

    private final SysMenuService menuService;

    public MenuController(SysMenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/menus")
    public Result<List<SysMenu>> menus() {
        // 骨架默认 org=1，后续从登录态/租户上下文获取
        return Result.ok(menuService.listEnabledMenus(1L));
    }
}
