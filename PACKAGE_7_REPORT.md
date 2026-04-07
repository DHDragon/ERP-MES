# 第七包：销售、采购、库存联调修正包 — 交付报告

> 目标：把销售主链路、采购主链路和库存基础层真正打通，消除模块间的接口断点、数量不一致和状态不一致问题。

## 0. 总结（结论）
- 已打通：
  - 销售出库审核 → 库存扣减 → 台账记录 → 订单/通知回写
  - 采购入库审核 → 库存增加 → 台账记录 → 订单/收货回写
  - 反审核（销售出库、采购入库）→ 库存/台账回滚 → 业务单据数量与状态回滚
- 已补齐：联动日志（`[INV-LINK]`）
- 已补充：单元测试（6 个用例）覆盖扣/加库存、回写一致性、反审核回滚、质检拦截

---

## 1) 本次修复点清单
### 1.1 销售链路
- ✅ 销售出库审核对接库存：从“占位 stub”改为调用 `InventoryService.outbound(...)`
- ✅ 销售出库反审核：新增反审核能力，执行库存/台账回滚 + 回写数量/状态
- ✅ 补齐联动日志：审核/逐行扣减/回滚都有日志

### 1.2 采购链路
- ✅ 采购入库审核对接库存：从“占位 stub”改为调用 `InventoryService.inbound(...)`
- ✅ 采购入库反审核：新增反审核能力，执行库存/台账回滚 + 回写数量/状态
- ✅ 补齐联动日志：审核/逐行入库/回滚都有日志

### 1.3 一致性修正
- ✅ 头/行数量口径统一：回写时用“行汇总”计算头累计数量
- ✅ 状态口径统一：按累计数量与目标数量计算 `OPEN/PARTIAL/DONE`（对应销售/采购各自状态字典）

---

## 2) 影响的接口清单
### 2.1 新增/增强接口（后端）
- 销售：
  - `POST /api/sales/outbounds/{id}/unaudit` 反审核销售出库
- 采购：
  - `POST /api/purchase/inbounds/{id}/unaudit` 反审核采购入库
- 库存（第六包基础层，被第七包联调使用）：
  - `POST /api/inventory/stock/query`
  - `GET  /api/inventory/ledger`
  - `POST /api/inventory/check`
  - `POST /api/inventory/rollback`
  - `GET/POST /api/inventory/transfers*`
  - `GET/POST /api/inventory/counts*`

---

## 3) 影响的数据库表清单
- 库存基础层（V7）：
  - `biz_inventory_stock`
  - `biz_inventory_ledger`
  - `biz_transfer_order_h`
  - `biz_transfer_order_d`
  - `biz_count_order_h`
  - `biz_count_order_d`
- 销售链路（回写影响）：
  - `biz_delivery_notice_h / biz_delivery_notice_d`
  - `biz_sales_order_h / biz_sales_order_d`
  - `biz_sales_outbound_h / biz_sales_outbound_d`
- 采购链路（回写影响）：
  - `biz_purchase_receipt_h / biz_purchase_receipt_d`
  - `biz_purchase_order_h / biz_purchase_order_d`
  - `biz_purchase_inbound_h / biz_purchase_inbound_d`

---

## 4) 回归测试清单（人工验收）
1. 销售出库审核：审核后库存减少，台账新增，发货通知/销售订单数量与状态回写正确
2. 采购入库审核：审核后库存增加，台账新增，收货/采购订单数量与状态回写正确
3. 反审核回滚：反审核后库存与台账回滚，所有回写数量恢复，状态恢复
4. 列表/详情一致：列表累计数量与详情行汇总一致
5. 质检拦截：`qcRequired=1 && qcPassed=0` 时禁止入库审核

---

## 5) 新增或修改文件清单
### 后端（新增）
- `backend/src/main/java/com/erp/inventory/**`（库存模块：controller/service/dto/entity/mapper）
- `backend/src/main/resources/db/migration/V7__inventory_base_layer.sql`
- `backend/src/test/java/com/erp/sales/service/SalesServiceLinkageTest.java`
- `backend/src/test/java/com/erp/purchase/service/PurchaseServiceLinkageTest.java`

### 后端（修改）
- `backend/src/main/java/com/erp/sales/service/InventoryStubService.java`
- `backend/src/main/java/com/erp/sales/service/SalesService.java`
- `backend/src/main/java/com/erp/sales/web/SalesController.java`
- `backend/src/main/java/com/erp/purchase/service/InventoryInboundStubService.java`
- `backend/src/main/java/com/erp/purchase/service/PurchaseService.java`
- `backend/src/main/java/com/erp/purchase/web/PurchaseController.java`
- `backend/pom.xml`（Surefire argLine：`-Dnet.bytebuddy.experimental=true`，适配 Java 25 下 Mockito/ByteBuddy）

### 前端（新增/修改）
- `frontend/src/api/modules/inventory.ts`（新增）
- `frontend/src/types/inventory.ts`（新增）
- `frontend/src/views/inventory/*`（新增：库存/台账/调拨/盘点页面）
- `frontend/src/router/index.ts`（新增 inventory 路由）

---

## 6) 必须补充的测试（已补齐）
- ✅ 销售出库扣库存测试：`SalesServiceLinkageTest.salesOutboundAudit_shouldDeductInventory...`
- ✅ 采购入库加库存测试：`PurchaseServiceLinkageTest.purchaseInboundAudit_shouldIncreaseInventory...`
- ✅ 回写数量一致性测试：同上两个测试断言（行→头、头→列表统计）
- ✅ 列表与详情统计一致性测试：`SalesServiceLinkageTest.orderExecutionSummary_shouldMatchHeaderStatistics`
- ✅ 反审核回滚测试：
  - `SalesServiceLinkageTest.salesOutboundUnAudit_shouldRollbackInventory...`
  - `PurchaseServiceLinkageTest.purchaseInboundUnAudit_shouldRollbackInventory...`

运行方式：
```bash
cd ERP-MES/backend
mvn -Dtest=SalesServiceLinkageTest,PurchaseServiceLinkageTest test
```

---

## 7) 备注
- 库存扣减策略：按你规则“允许超领，不审批”，出库不足时不拦截，库存可为负。
- 若后续需要“可配置是否允许负库存/超领”，建议在 InventoryService 层做开关与策略注入。

