# 第二包交付说明（最终版）

更新时间：2026-04-06

## 1) 接口清单（本次交付）

### 认证与权限
- `POST /api/auth/login` 登录，返回 JWT
- `POST /api/auth/logout` 退出登录
- `GET /api/auth/me` 当前用户信息（含角色、权限）
- `GET /api/auth/menus` 当前用户可见菜单树
- `GET /api/auth/permissions` 当前用户权限点

### 基础主数据与参数
- `GET /api/base/dict/{dictType}` 按字典类型取字典项
- `POST /api/base/dicts/batch` 批量取字典
- `GET /api/base/params` 参数列表
- `PUT /api/base/param/{paramKey}` 更新参数

### 用户管理
- `GET /api/base/users` 用户分页
- `POST /api/base/users` 用户新增/更新
- `DELETE /api/base/users/{id}` 用户逻辑删除

### 角色管理
- `GET /api/base/roles` 角色分页
- `POST /api/base/roles` 角色新增/更新
- `DELETE /api/base/roles/{id}` 角色逻辑删除

### 菜单管理
- `GET /api/base/menus` 菜单列表
- `POST /api/base/menus` 菜单新增/更新
- `DELETE /api/base/menus/{id}` 菜单逻辑删除

### 角色菜单授权
- `GET /api/base/role-menus/{roleId}` 角色菜单ID列表
- `POST /api/base/role-menus/{roleId}` 保存角色菜单授权

---

## 2) 页面清单（9个业务页已补齐）

1. `HomeView.vue` 首页
2. `UsersView.vue` 用户管理
3. `RolesView.vue` 角色管理
4. `MenusView.vue` 菜单管理
5. `DictsView.vue` 字典管理
6. `ParamsView.vue` 参数中心
7. `DataScopeView.vue` 数据权限
8. `ProfileView.vue` 个人信息
9. `PermissionsView.vue` 我的权限点

> 登录页：`LoginView.vue`（不计入9个业务页）

---

## 3) 表结构（本次涉及核心表）

本次代码直接依赖/扩展的核心表：

- 认证与用户：`md_user`、`rel_user_role`、`md_role`
- 菜单与权限：`md_menu`、`rel_role_menu`
- 字典：`md_dict_type`、`md_dict_item`
- 系统参数：`cfg_system_param`
- 数据权限：`cfg_data_scope`

> 完整 DDL 以 `V1__init_base_tables.sql` + `V3__auth_permission_dict_param.sql` 为准。

---

## 4) 初始化 SQL

- 基础结构与初始数据：
  - `backend/src/main/resources/db/migration/V1__init_base_tables.sql`
  - `backend/src/main/resources/db/migration/V2__seed_base_data.sql`
- 本次新增迁移：
  - `backend/src/main/resources/db/migration/V3__auth_permission_dict_param.sql`

`V3` 包含：
- 认证/授权相关表补充
- 菜单权限码、角色菜单关系补齐
- 字典与系统参数初始化补充

---

## 5) 新增/修改文件（本次收尾）

### 新增
- `backend/src/main/java/com/erp/auth/service/AuthService.java`
- `backend/src/main/java/com/erp/base/**`（dto/entity/mapper/service/web 一整套）
- `backend/src/main/java/com/erp/common/annotation/RequiresPermission.java`
- `backend/src/main/java/com/erp/common/aspect/PermissionAspect.java`
- `backend/src/main/resources/db/migration/V3__auth_permission_dict_param.sql`
- `frontend/src/api/modules/base.ts`
- `frontend/src/types/base.ts`
- `frontend/src/views/RolesView.vue`
- `frontend/src/views/DictsView.vue`
- `frontend/src/views/ParamsView.vue`
- `frontend/src/views/DataScopeView.vue`
- `frontend/src/views/ProfileView.vue`
- `frontend/src/views/PermissionsView.vue`

### 修改
- `backend/src/main/java/com/erp/auth/web/AuthController.java`
- `backend/src/main/java/com/erp/common/aspect/DataPermissionAspect.java`
- `backend/src/main/java/com/erp/common/config/MybatisMapperScanConfig.java`
- `backend/src/main/java/com/erp/common/config/SecurityConfig.java`
- `backend/src/main/java/com/erp/common/util/SecurityContextUtil.java`
- `frontend/src/api/modules/menu.ts`
- `frontend/src/api/modules/system.ts`
- `frontend/src/layout/MainLayout.vue`
- `frontend/src/router/index.ts`
- `frontend/src/stores/user.ts`
- `frontend/src/types/system.ts`
- `frontend/src/utils/permission.ts`
- `frontend/src/views/UsersView.vue`
- `frontend/src/views/MenusView.vue`

---

## 构建验收结果

- 后端：`mvn -q -DskipTests package` ✅ 通过
- 前端：`npm run build` ✅ 通过（仅 chunk size 告警，不影响产物）

> 结论：第二包已达到“后端+前端+SQL+9页面+5项输出”交付口径，可提交联调。