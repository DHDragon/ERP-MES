package com.erp.base.web;

import com.erp.base.entity.CfgSystemParam;
import com.erp.base.entity.MdMenu;
import com.erp.base.entity.MdRole;
import com.erp.base.entity.MdUser;
import com.erp.base.service.BaseService;
import com.erp.common.annotation.DataPermission;
import com.erp.common.annotation.RequiresPermission;
import com.erp.common.dto.PageResult;
import com.erp.common.dto.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/base")
public class BaseController {

    private final BaseService baseService;

    public BaseController(BaseService baseService) {
        this.baseService = baseService;
    }

    @GetMapping("/dict/{dictType}")
    public Result<List<Map<String, Object>>> dict(@PathVariable String dictType) {
        return Result.ok(baseService.dictByType(dictType));
    }

    @PostMapping("/dicts/batch")
    public Result<Map<String, List<Map<String, Object>>>> dictBatch(@RequestBody DictBatchReq req) {
        return Result.ok(baseService.batchDict(req.getDictTypes()));
    }

    @GetMapping("/params")
    @RequiresPermission("sys:param:list")
    public Result<List<CfgSystemParam>> params() {
        return Result.ok(baseService.listParams());
    }

    @PutMapping("/param/{paramKey}")
    @RequiresPermission("sys:param:edit")
    public Result<CfgSystemParam> updateParam(@PathVariable String paramKey, @RequestBody ParamUpdateReq req) {
        return Result.ok(baseService.updateParam(paramKey, req.getParamValue()));
    }

    @GetMapping("/users")
    @RequiresPermission("sys:user:list")
    @DataPermission(resource = "md_user")
    public Result<PageResult<MdUser>> users(@RequestParam(defaultValue = "1") Integer pageNo,
                                            @RequestParam(defaultValue = "20") Integer pageSize,
                                            @RequestParam(required = false) String keyword) {
        return Result.ok(baseService.pageUsers(pageNo, pageSize, keyword));
    }

    @PostMapping("/users")
    @RequiresPermission("sys:user:add")
    public Result<MdUser> saveUser(@RequestBody MdUser req) {
        return Result.ok(baseService.saveUser(req));
    }

    @DeleteMapping("/users/{id}")
    @RequiresPermission("sys:user:delete")
    public Result<String> deleteUser(@PathVariable Long id) {
        baseService.deleteUser(id);
        return Result.ok("OK");
    }

    @GetMapping("/roles")
    @RequiresPermission("sys:role:list")
    public Result<PageResult<MdRole>> roles(@RequestParam(defaultValue = "1") Integer pageNo,
                                            @RequestParam(defaultValue = "20") Integer pageSize,
                                            @RequestParam(required = false) String keyword) {
        return Result.ok(baseService.pageRoles(pageNo, pageSize, keyword));
    }

    @PostMapping("/roles")
    @RequiresPermission("sys:role:edit")
    public Result<MdRole> saveRole(@RequestBody MdRole req) {
        return Result.ok(baseService.saveRole(req));
    }

    @DeleteMapping("/roles/{id}")
    @RequiresPermission("sys:role:edit")
    public Result<String> deleteRole(@PathVariable Long id) {
        baseService.deleteRole(id);
        return Result.ok("OK");
    }

    @GetMapping("/menus")
    @RequiresPermission("sys:menu:list")
    public Result<List<MdMenu>> menus() {
        return Result.ok(baseService.listMenus());
    }

    @PostMapping("/menus")
    @RequiresPermission("sys:menu:edit")
    public Result<MdMenu> saveMenu(@RequestBody MdMenu req) {
        return Result.ok(baseService.saveMenu(req));
    }

    @DeleteMapping("/menus/{id}")
    @RequiresPermission("sys:menu:edit")
    public Result<String> deleteMenu(@PathVariable Long id) {
        baseService.deleteMenu(id);
        return Result.ok("OK");
    }

    @GetMapping("/role-menus/{roleId}")
    @RequiresPermission("sys:role:edit")
    public Result<List<Long>> roleMenus(@PathVariable Long roleId) {
        return Result.ok(baseService.roleMenuIds(roleId));
    }

    @PostMapping("/role-menus/{roleId}")
    @RequiresPermission("sys:role:edit")
    public Result<List<Long>> saveRoleMenus(@PathVariable Long roleId, @RequestBody RoleMenusReq req) {
        return Result.ok(baseService.saveRoleMenus(roleId, req.getMenuIds()));
    }

    public static class DictBatchReq {
        private List<String> dictTypes;
        public List<String> getDictTypes() { return dictTypes; }
        public void setDictTypes(List<String> dictTypes) { this.dictTypes = dictTypes; }
    }

    public static class ParamUpdateReq {
        private String paramValue;
        public String getParamValue() { return paramValue; }
        public void setParamValue(String paramValue) { this.paramValue = paramValue; }
    }

    public static class RoleMenusReq {
        private List<Long> menuIds;
        public List<Long> getMenuIds() { return menuIds; }
        public void setMenuIds(List<Long> menuIds) { this.menuIds = menuIds; }
    }
}
