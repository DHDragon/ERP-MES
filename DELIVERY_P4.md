# 第四包交付说明：销售主链路

更新时间：2026-04-06

## 1) 接口清单

统一前缀：`/api/sales`

### 销售订单
- `GET /orders` 列表
- `GET /orders/{id}` 详情
- `POST /orders` 新增/编辑
- `POST /orders/{id}/submit` 提交审批
- `POST /orders/{id}/audit` 审核
- `POST /orders/{id}/unaudit` 反审核
- `POST /orders/{id}/close` 关闭
- `POST /orders/{id}/void` 作废
- `POST /orders/push-delivery-notice` 下推发货通知

### 发货通知
- `GET /delivery-notices` 列表
- `GET /delivery-notices/{id}` 详情
- `POST /delivery-notices` 新增/编辑
- `POST /delivery-notices/{id}/audit` 审核
- `POST /delivery-notices/{id}/close` 关闭

### 销售出库
- `GET /outbounds` 列表
- `GET /outbounds/{id}` 详情
- `POST /outbounds` 新增/编辑
- `POST /outbounds/{id}/audit` 审核
- `GET /outbounds/{id}/pda-records` PDA记录

### 销售退货（基础骨架）
- `GET /returns` 列表
- `GET /returns/{id}` 详情
- `POST /returns` 新增/编辑
- `POST /returns/{id}/audit` 审核

### 订单执行汇总
- `GET /order-executions`

---

## 2) 页面清单

### 销售订单
- `SalesOrderListView.vue`
- `SalesOrderFormView.vue`
- `SalesOrderDetailView.vue`

### 发货通知
- `DeliveryNoticeListView.vue`
- `DeliveryNoticeFormView.vue`
- `DeliveryNoticeDetailView.vue`

### 销售出库
- `SalesOutboundListView.vue`
- `SalesOutboundDetailView.vue`

### 销售退货
- `SalesReturnListView.vue`
- `SalesReturnFormView.vue`

---

## 3) 数据表清单
- `biz_sales_order_h`
- `biz_sales_order_d`
- `biz_delivery_notice_h`
- `biz_delivery_notice_d`
- `biz_sales_outbound_h`
- `biz_sales_outbound_d`
- `biz_sales_return_h`
- `biz_sales_return_d`

迁移文件：
- `backend/src/main/resources/db/migration/V5__sales_main_chain.sql`

---

## 4) 核心流程说明

1. 销售订单保存后进入 `DRAFT`，提审后 `SUBMITTED`，审核通过后 `APPROVED`。
2. 仅 `APPROVED` 的销售订单允许下推发货通知。
3. 下推发货通知时，逐行校验：下推数量 <= 订单未发数量；超量直接阻断。
4. 销售出库保存时，逐行校验：出库数量 <= 发货通知剩余数量；超量直接阻断。
5. 销售出库审核后，系统自动回写：
   - 发货通知行/头累计出库数量和状态（OPEN/PARTIAL/DONE）
   - 销售订单行/头累计发货数量和状态（APPROVED/PARTIAL/DONE）
6. 销售订单支持部分发货、全部发货、关闭、作废；已执行数量>0时阻断反审核/作废。
7. 出库审核后调用 `InventoryStubService.deductForSalesOutbound` 作为库存扣减占位接口。

---

## 5) 新增文件清单（第四包）

### 后端
- `backend/src/main/resources/db/migration/V5__sales_main_chain.sql`
- `backend/src/main/java/com/erp/sales/entity/*`（8个实体）
- `backend/src/main/java/com/erp/sales/mapper/*`（8个Mapper）
- `backend/src/main/java/com/erp/sales/dto/SalesDtos.java`
- `backend/src/main/java/com/erp/sales/service/SalesNoGenerator.java`
- `backend/src/main/java/com/erp/sales/service/InventoryStubService.java`
- `backend/src/main/java/com/erp/sales/service/SalesService.java`
- `backend/src/main/java/com/erp/sales/web/SalesController.java`

### 前端
- `frontend/src/types/sales.ts`
- `frontend/src/api/modules/sales.ts`
- `frontend/src/views/sales/SalesOrderListView.vue`
- `frontend/src/views/sales/SalesOrderFormView.vue`
- `frontend/src/views/sales/SalesOrderDetailView.vue`
- `frontend/src/views/sales/DeliveryNoticeListView.vue`
- `frontend/src/views/sales/DeliveryNoticeFormView.vue`
- `frontend/src/views/sales/DeliveryNoticeDetailView.vue`
- `frontend/src/views/sales/SalesOutboundListView.vue`
- `frontend/src/views/sales/SalesOutboundDetailView.vue`
- `frontend/src/views/sales/SalesReturnListView.vue`
- `frontend/src/views/sales/SalesReturnFormView.vue`

### 修改
- `frontend/src/router/index.ts`
- `frontend/src/layout/MainLayout.vue`

---

## 构建验收
- 后端：`mvn -q -DskipTests package` ✅
- 前端：`npm run build` ✅

> 结论：销售订单→发货通知→销售出库主链路已可在系统内完整走通，含数量回写与状态回写；超量下推已阻断；销售退货提供基础 CRUD+审核骨架。