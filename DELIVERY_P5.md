# 第五包交付说明：采购主链路

更新时间：2026-04-06

## 1) 接口清单

统一前缀：`/api/purchase`

### 采购申请（Purchase Req）
- `GET /reqs` 列表
- `GET /reqs/{id}` 详情
- `POST /reqs` 新增/编辑
- `POST /reqs/{id}/submit` 提交审批
- `POST /reqs/{id}/audit` 审核
- `POST /reqs/push-order` 下推采购订单

### 采购订单（Purchase Order）
- `GET /orders` 列表
- `GET /orders/{id}` 详情
- `POST /orders` 新增/编辑
- `POST /orders/{id}/audit` 审核
- `POST /orders/{id}/close` 关闭

### 收货/到货（Receipt）
- `GET /receipts` 列表
- `GET /receipts/{id}` 详情
- `POST /receipts/confirm` 确认收货

### 采购入库（Inbound）
- `GET /inbounds` 列表
- `GET /inbounds/{id}` 详情
- `POST /inbounds` 新增/编辑
- `POST /inbounds/{id}/audit` 审核

### 采购退货（基础骨架）
- `GET /returns` 列表
- `GET /returns/{id}` 详情
- `POST /returns` 新增/编辑
- `POST /returns/{id}/audit` 审核

---

## 2) 页面清单

### 采购申请
- `PurchaseReqListView.vue`
- `PurchaseReqFormView.vue`
- `PurchaseReqDetailView.vue`

### 采购订单
- `PurchaseOrderListView.vue`
- `PurchaseOrderFormView.vue`
- `PurchaseOrderDetailView.vue`

### 收货/到货
- `PurchaseReceiptListView.vue`
- `PurchaseReceiptDetailView.vue`

### 采购入库
- `PurchaseInboundListView.vue`
- `PurchaseInboundDetailView.vue`

### 采购退货
- `PurchaseReturnListView.vue`
- `PurchaseReturnFormView.vue`

---

## 3) 数据表清单
迁移文件：`backend/src/main/resources/db/migration/V6__purchase_main_chain.sql`

- `biz_purchase_req_h`
- `biz_purchase_req_d`
- `biz_purchase_order_h`
- `biz_purchase_order_d`
- `biz_purchase_receipt_h`
- `biz_purchase_receipt_d`
- `biz_purchase_inbound_h`
- `biz_purchase_inbound_d`
- `biz_purchase_return_h`
- `biz_purchase_return_d`

---

## 4) 回写逻辑说明（核心）

### A. 采购申请 → 下推采购订单（push-order）
- 前置条件：采购申请 `approvalStatus=APPROVED`。
- 校验：下推数量 `qty` 必须 <= 申请行 `reqQty - orderedQty`（超下推阻断）。
- 回写：
  - 申请行 `orderedQty += qty`，状态 `OPEN/PARTIAL/DONE`。
  - 申请头汇总 `orderedQty`，状态 `PARTIAL/DONE`。

### B. 采购订单 → 确认收货（receipts/confirm）
- 前置条件：采购订单已审核通过（`approvalStatus=APPROVED`）或处于部分状态。
- 校验：收货数量 `receiptQty` 必须 <= 订单行 `orderQty - receivedQty`（超收阻断）。
- 回写：
  - 订单行 `receivedQty += receiptQty`，状态 `PARTIAL/RECEIVED`。
  - 订单头汇总 `receivedQty`，状态 `PARTIAL/RECEIVED`。

### C. 收货 → 采购入库审核（inbounds/{id}/audit）
- 质检挂点：
  - 入库头字段：`qcRequired/qcPassed/inspectionHookStatus`。
  - `qcRequired=1` 时必须 `qcPassed=1` 才允许审核入库（后续可替换为 IQC 服务判断）。
- 校验：入库数量 `inboundQty` 必须 <= 收货行 `receiptQty - inboundQty`（超入库阻断）。
- 回写：
  - 收货行 `inboundQty += inboundQty`，状态 `PARTIAL/DONE`。
  - 收货头汇总 `inboundQty`，状态 `PARTIAL/DONE`。
  - 订单行 `inboundQty += inboundQty`，状态 `PARTIAL/DONE`。
  - 订单头汇总 `inboundQty`，状态 `PARTIAL/DONE`。
  - （入库审核后）调用库存台账占位：`InventoryInboundStubService.writeLedgerForPurchaseInbound(inboundId)`。

---

## 5) 新增文件清单（第五包）

### 后端
- `backend/src/main/resources/db/migration/V6__purchase_main_chain.sql`
- `backend/src/main/java/com/erp/purchase/entity/*`（10个实体）
- `backend/src/main/java/com/erp/purchase/mapper/*`（10个Mapper）
- `backend/src/main/java/com/erp/purchase/dto/PurchaseDtos.java`
- `backend/src/main/java/com/erp/purchase/service/PurchaseNoGenerator.java`
- `backend/src/main/java/com/erp/purchase/service/PurchaseService.java`
- `backend/src/main/java/com/erp/purchase/service/QualityInspectionStubService.java`（来料检验挂点）
- `backend/src/main/java/com/erp/purchase/service/InventoryInboundStubService.java`（库存台账挂点）
- `backend/src/main/java/com/erp/purchase/web/PurchaseController.java`

### 前端
- `frontend/src/types/purchase.ts`
- `frontend/src/api/modules/purchase.ts`
- `frontend/src/views/purchase/PurchaseReqListView.vue`
- `frontend/src/views/purchase/PurchaseReqFormView.vue`
- `frontend/src/views/purchase/PurchaseReqDetailView.vue`
- `frontend/src/views/purchase/PurchaseOrderListView.vue`
- `frontend/src/views/purchase/PurchaseOrderFormView.vue`
- `frontend/src/views/purchase/PurchaseOrderDetailView.vue`
- `frontend/src/views/purchase/PurchaseReceiptListView.vue`
- `frontend/src/views/purchase/PurchaseReceiptDetailView.vue`
- `frontend/src/views/purchase/PurchaseInboundListView.vue`
- `frontend/src/views/purchase/PurchaseInboundDetailView.vue`
- `frontend/src/views/purchase/PurchaseReturnListView.vue`
- `frontend/src/views/purchase/PurchaseReturnFormView.vue`

### 修改
- `frontend/src/router/index.ts`
- `frontend/src/layout/MainLayout.vue`

---

## 测试覆盖（按验收用例映射）
- 用例1：采购申请下推采购订单 ✅（push-order）
- 用例2：采购订单收货 ✅（receipts/confirm + 超收阻断）
- 用例3：收货入库 ✅（inbounds/save + audit）
- 用例4：回写数量测试 ✅（订单/收货/入库多层回写）
- 用例5：超收阻断测试 ✅（收货与入库均阻断超量）

## 构建验收
- 后端：`mvn -q -DskipTests package` ✅
- 前端：`npm run build` ✅

> 结论：采购申请→采购订单→收货→采购入库主链路已可跑通，含数量/状态回写、超收阻断；来料检验与库存台账均已预留挂点。