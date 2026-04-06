package com.erp.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.erp.base.entity.*;
import com.erp.base.mapper.*;
import com.erp.common.dto.PageResult;
import com.erp.common.exception.BizException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BaseService {

    private final MdDictTypeMapper dictTypeMapper;
    private final MdDictItemMapper dictItemMapper;
    private final CfgSystemParamMapper paramMapper;
    private final MdUserMapper userMapper;
    private final MdRoleMapper roleMapper;
    private final MdMenuMapper menuMapper;
    private final RelRoleMenuMapper roleMenuMapper;

    public BaseService(MdDictTypeMapper dictTypeMapper, MdDictItemMapper dictItemMapper, CfgSystemParamMapper paramMapper,
                       MdUserMapper userMapper, MdRoleMapper roleMapper, MdMenuMapper menuMapper, RelRoleMenuMapper roleMenuMapper) {
        this.dictTypeMapper = dictTypeMapper;
        this.dictItemMapper = dictItemMapper;
        this.paramMapper = paramMapper;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.menuMapper = menuMapper;
        this.roleMenuMapper = roleMenuMapper;
    }

    public List<Map<String, Object>> dictByType(String dictType) {
        MdDictType t = dictTypeMapper.selectOne(new QueryWrapper<MdDictType>().eq("org_id",1).eq("dict_type",dictType).eq("del_flag",0).last("limit 1"));
        if (t == null) return List.of();
        return dictItemMapper.selectList(new QueryWrapper<MdDictItem>().eq("org_id",1).eq("dict_type_id",t.getId()).eq("del_flag",0).orderByAsc("item_sort"))
                .stream().map(x -> {
                    Map<String,Object> m = new LinkedHashMap<>();
                    m.put("code", x.getItemCode()); m.put("name", x.getItemName()); m.put("sort", x.getItemSort()); m.put("status", x.getStatus());
                    return m;
                }).toList();
    }

    public Map<String, List<Map<String, Object>>> batchDict(List<String> dictTypes) {
        Map<String, List<Map<String, Object>>> ret = new LinkedHashMap<>();
        for (String t : dictTypes) ret.put(t, dictByType(t));
        return ret;
    }

    public List<CfgSystemParam> listParams() {
        return paramMapper.selectList(new QueryWrapper<CfgSystemParam>().eq("org_id",1).eq("del_flag",0).orderByAsc("id"));
    }

    public CfgSystemParam updateParam(String key, String value) {
        CfgSystemParam p = paramMapper.selectOne(new QueryWrapper<CfgSystemParam>().eq("org_id",1).eq("param_key", key).eq("del_flag",0).last("limit 1"));
        if (p == null) throw new BizException(404, "参数不存在: " + key);
        p.setParamValue(value);
        paramMapper.updateById(p);
        return p;
    }

    public PageResult<MdUser> pageUsers(int pageNo, int pageSize, String keyword) {
        List<MdUser> all = userMapper.selectList(new QueryWrapper<MdUser>().eq("org_id",1).eq("del_flag",0).orderByDesc("id"));
        if (keyword != null && !keyword.isBlank()) all = all.stream().filter(u -> (u.getUsername()+" "+u.getRealName()).contains(keyword)).toList();
        return paginate(all, pageNo, pageSize);
    }

    public MdUser saveUser(MdUser u) {
        if (u.getId() == null) userMapper.insert(u);
        else userMapper.updateById(u);
        return u;
    }

    public void deleteUser(Long id) {
        MdUser u = userMapper.selectById(id);
        if (u == null) return;
        u.setDelFlag(1);
        userMapper.updateById(u);
    }

    public PageResult<MdRole> pageRoles(int pageNo, int pageSize, String keyword) {
        List<MdRole> all = roleMapper.selectList(new QueryWrapper<MdRole>().eq("org_id",1).eq("del_flag",0).orderByDesc("id"));
        if (keyword != null && !keyword.isBlank()) all = all.stream().filter(u -> (u.getRoleCode()+" "+u.getRoleName()).contains(keyword)).toList();
        return paginate(all, pageNo, pageSize);
    }

    public MdRole saveRole(MdRole r) {
        if (r.getId() == null) roleMapper.insert(r); else roleMapper.updateById(r);
        return r;
    }

    public void deleteRole(Long id) {
        MdRole x = roleMapper.selectById(id); if (x == null) return; x.setDelFlag(1); roleMapper.updateById(x);
    }

    public List<MdMenu> listMenus() {
        return menuMapper.selectList(new QueryWrapper<MdMenu>().eq("org_id",1).eq("del_flag",0).orderByAsc("sort_no"));
    }

    public MdMenu saveMenu(MdMenu m) {
        if (m.getId() == null) menuMapper.insert(m); else menuMapper.updateById(m);
        return m;
    }

    public void deleteMenu(Long id) {
        MdMenu x = menuMapper.selectById(id); if (x == null) return; x.setDelFlag(1); menuMapper.updateById(x);
    }

    public List<Long> roleMenuIds(Long roleId) {
        return roleMenuMapper.selectList(new QueryWrapper<RelRoleMenu>().eq("org_id",1).eq("role_id", roleId).eq("del_flag",0))
                .stream().map(RelRoleMenu::getMenuId).toList();
    }

    public List<Long> saveRoleMenus(Long roleId, List<Long> menuIds) {
        List<RelRoleMenu> old = roleMenuMapper.selectList(new QueryWrapper<RelRoleMenu>().eq("org_id",1).eq("role_id", roleId).eq("del_flag",0));
        for (RelRoleMenu x : old) {
            x.setDelFlag(1);
            roleMenuMapper.updateById(x);
        }
        Set<Long> uniq = new LinkedHashSet<>(menuIds == null ? List.of() : menuIds);
        for (Long menuId : uniq) {
            RelRoleMenu r = new RelRoleMenu();
            r.setOrgId(1L); r.setRoleId(roleId); r.setMenuId(menuId); r.setDelFlag(0);
            roleMenuMapper.insert(r);
        }
        return roleMenuIds(roleId);
    }

    private <T> PageResult<T> paginate(List<T> all, int pageNo, int pageSize) {
        int from = Math.max(0, (pageNo - 1) * pageSize);
        int to = Math.min(all.size(), from + pageSize);
        List<T> rows = from >= all.size() ? List.of() : all.subList(from, to);
        return PageResult.of(all.size(), pageNo, pageSize, rows);
    }
}
