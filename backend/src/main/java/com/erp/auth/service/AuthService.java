package com.erp.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.erp.base.dto.MenuNodeDto;
import com.erp.base.entity.MdMenu;
import com.erp.base.entity.MdRole;
import com.erp.base.entity.MdUser;
import com.erp.base.entity.RelRoleMenu;
import com.erp.base.entity.RelUserRole;
import com.erp.base.mapper.*;
import com.erp.common.exception.BizException;
import com.erp.common.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final MdUserMapper userMapper;
    private final MdRoleMapper roleMapper;
    private final MdMenuMapper menuMapper;
    private final RelUserRoleMapper userRoleMapper;
    private final RelRoleMenuMapper roleMenuMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(MdUserMapper userMapper, MdRoleMapper roleMapper, MdMenuMapper menuMapper,
                       RelUserRoleMapper userRoleMapper, RelRoleMenuMapper roleMenuMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.menuMapper = menuMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMenuMapper = roleMenuMapper;
        this.jwtUtil = jwtUtil;
    }

    public String login(String username, String password) {
        MdUser u = userMapper.selectOne(new QueryWrapper<MdUser>()
                .eq("org_id", 1)
                .eq("username", username)
                .eq("del_flag", 0)
                .last("limit 1"));
        if (u == null) throw new BizException(401, "用户名或密码错误");
        String hash = u.getPasswordHash();
        boolean ok;
        if (hash != null && hash.startsWith("$2")) {
            ok = encoder.matches(password, hash);
        } else {
            ok = Objects.equals(password, hash);
            if (ok) {
                u.setPasswordHash(encoder.encode(password));
                userMapper.updateById(u);
            }
        }
        if (!ok) throw new BizException(401, "用户名或密码错误");
        return jwtUtil.createToken(username);
    }

    public Map<String, Object> me(String username) {
        MdUser u = userMapper.selectOne(new QueryWrapper<MdUser>().eq("org_id",1).eq("username", username).eq("del_flag",0).last("limit 1"));
        if (u == null) throw new BizException(404, "用户不存在");
        List<String> roles = listRoleCodesByUserId(u.getId());
        List<String> perms = listPermissionsByUserId(u.getId());
        Map<String, Object> ret = new LinkedHashMap<>();
        ret.put("id", u.getId());
        ret.put("username", u.getUsername());
        ret.put("realName", u.getRealName());
        ret.put("orgId", u.getOrgId());
        ret.put("deptId", u.getDeptId());
        ret.put("roles", roles);
        ret.put("permissions", perms);
        return ret;
    }

    public List<String> listPermissionsByUsername(String username) {
        MdUser u = userMapper.selectOne(new QueryWrapper<MdUser>().eq("org_id",1).eq("username", username).eq("del_flag",0).last("limit 1"));
        if (u == null) return List.of();
        return listPermissionsByUserId(u.getId());
    }

    private List<String> listRoleCodesByUserId(Long userId) {
        List<RelUserRole> urs = userRoleMapper.selectList(new QueryWrapper<RelUserRole>().eq("org_id",1).eq("user_id", userId).eq("del_flag",0));
        if (urs.isEmpty()) return List.of();
        List<Long> roleIds = urs.stream().map(RelUserRole::getRoleId).distinct().toList();
        List<MdRole> roles = roleMapper.selectList(new QueryWrapper<MdRole>().in("id", roleIds).eq("del_flag",0));
        return roles.stream().map(MdRole::getRoleCode).toList();
    }

    private List<String> listPermissionsByUserId(Long userId) {
        List<Long> roleIds = userRoleMapper.selectList(new QueryWrapper<RelUserRole>().eq("org_id",1).eq("user_id",userId).eq("del_flag",0))
                .stream().map(RelUserRole::getRoleId).distinct().toList();
        if (roleIds.isEmpty()) return List.of();
        List<Long> menuIds = roleMenuMapper.selectList(new QueryWrapper<RelRoleMenu>().eq("org_id",1).in("role_id", roleIds).eq("del_flag",0))
                .stream().map(RelRoleMenu::getMenuId).distinct().toList();
        if (menuIds.isEmpty()) return List.of();
        return menuMapper.selectList(new QueryWrapper<MdMenu>().in("id", menuIds).eq("del_flag",0))
                .stream().map(MdMenu::getPermissionCode).filter(s -> s != null && !s.isBlank()).distinct().toList();
    }

    public List<MenuNodeDto> menusByUsername(String username) {
        MdUser u = userMapper.selectOne(new QueryWrapper<MdUser>().eq("org_id",1).eq("username",username).eq("del_flag",0).last("limit 1"));
        if (u == null) return List.of();
        List<Long> roleIds = userRoleMapper.selectList(new QueryWrapper<RelUserRole>().eq("org_id",1).eq("user_id",u.getId()).eq("del_flag",0))
                .stream().map(RelUserRole::getRoleId).distinct().toList();
        if (roleIds.isEmpty()) return List.of();
        List<Long> menuIds = roleMenuMapper.selectList(new QueryWrapper<RelRoleMenu>().eq("org_id",1).in("role_id",roleIds).eq("del_flag",0))
                .stream().map(RelRoleMenu::getMenuId).distinct().toList();
        if (menuIds.isEmpty()) return List.of();
        List<MdMenu> menus = menuMapper.selectList(new QueryWrapper<MdMenu>().in("id",menuIds).eq("del_flag",0).eq("status","ENABLED").orderByAsc("sort_no"));
        List<MdMenu> onlyMenu = menus.stream().filter(m -> "MENU".equals(m.getMenuType())).toList();
        Map<Long, MenuNodeDto> byId = new LinkedHashMap<>();
        for (MdMenu m : onlyMenu) {
            MenuNodeDto d = new MenuNodeDto();
            d.setId(m.getId()); d.setParentId(m.getParentId()); d.setMenuCode(m.getMenuCode()); d.setMenuName(m.getMenuName());
            d.setMenuType(m.getMenuType()); d.setPath(m.getPath()); d.setComponent(m.getComponent()); d.setIcon(m.getIcon()); d.setPermissionCode(m.getPermissionCode()); d.setSortNo(m.getSortNo());
            byId.put(d.getId(), d);
        }
        List<MenuNodeDto> roots = new ArrayList<>();
        for (MenuNodeDto d : byId.values()) {
            if (d.getParentId() == null || !byId.containsKey(d.getParentId())) roots.add(d);
            else byId.get(d.getParentId()).getChildren().add(d);
        }
        return roots.stream().sorted(Comparator.comparing(x -> Optional.ofNullable(x.getSortNo()).orElse(0))).collect(Collectors.toList());
    }
}
