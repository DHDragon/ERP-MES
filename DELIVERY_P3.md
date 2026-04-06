# 第三包交付说明：主数据中心

更新时间：2026-04-06

## 一、后端模块（已完成）
- 物料主数据：`md_material`
- 物料分类：`md_material_category`
- 客户主数据：`md_customer`
- 供应商主数据：`md_supplier`
- 仓库主数据：`md_warehouse`
- 库位主数据：`md_location`

代码目录：
- `backend/src/main/java/com/erp/masterdata/entity/*`
- `backend/src/main/java/com/erp/masterdata/mapper/*`
- `backend/src/main/java/com/erp/masterdata/service/MasterDataService.java`
- `backend/src/main/java/com/erp/masterdata/web/MasterDataController.java`

## 二、接口能力（6个模块均实现）
每个模块均支持：
1. 列表查询
2. 详情查询
3. 新增/编辑（统一 save）
4. 提交审批
5. 审核
6. 启用
7. 停用
8. 导入
9. 导出（骨架）

统一前缀：`/api/masterdata/*`

## 三、前端页面（已完成）
### 物料
- `MaterialListView.vue`
- `MaterialFormView.vue`
- `MaterialDetailView.vue`

### 物料分类
- `MaterialCategoryView.vue`

### 客户
- `CustomerListView.vue`
- `CustomerFormView.vue`
- `CustomerDetailView.vue`

### 供应商
- `SupplierListView.vue`
- `SupplierFormView.vue`
- `SupplierDetailView.vue`

### 仓库
- `WarehouseListView.vue`
- `WarehouseFormView.vue`

### 库位
- `LocationListView.vue`
- `LocationFormView.vue`

前端新增支持：
- `frontend/src/api/modules/masterdata.ts`
- `frontend/src/types/masterdata.ts`
- 路由已接入（`frontend/src/router/index.ts`）
- 主导航已接入“主数据中心”（`frontend/src/layout/MainLayout.vue`）

## 四、业务规则落地
- 物料编码唯一 ✅
- 客户编码唯一 ✅
- 供应商编码唯一 ✅
- 仓库编码唯一 ✅
- 启用前需审批通过（APPROVED）✅
- 删除策略：不提供物理删除接口，以停用/逻辑删除为主 ✅

## 五、数据库迁移
新增：
- `backend/src/main/resources/db/migration/V4__masterdata_center.sql`

包含：
- 6张主数据表建表脚本
- 唯一索引
- 审批状态字段
- 初始演示数据

## 六、构建验证
- 后端：`mvn -q -DskipTests package` ✅
- 前端：`npm run build` ✅（仅 chunk 体积告警，不影响构建）
