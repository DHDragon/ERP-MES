-- 第五包：采购主链路（采购申请 -> 采购订单 -> 收货 -> 采购入库）

CREATE TABLE IF NOT EXISTS biz_purchase_req_h (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  req_no VARCHAR(64) NOT NULL,
  source_type VARCHAR(32) NOT NULL DEFAULT 'MANUAL',
  supplier_id BIGINT NULL,
  biz_date DATE NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  approval_status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
  total_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  ordered_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  remark VARCHAR(255) NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_purchase_req_no(org_id, req_no)
) COMMENT='采购申请头';

CREATE TABLE IF NOT EXISTS biz_purchase_req_d (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  req_id BIGINT NOT NULL,
  line_no INT NOT NULL,
  material_id BIGINT NOT NULL,
  req_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  ordered_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
  remark VARCHAR(255) NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_purchase_req_line(org_id, req_id, line_no),
  KEY idx_purchase_req_d_h(req_id)
) COMMENT='采购申请行';

CREATE TABLE IF NOT EXISTS biz_purchase_order_h (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  order_no VARCHAR(64) NOT NULL,
  req_id BIGINT NULL,
  supplier_id BIGINT NULL,
  biz_date DATE NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  approval_status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
  total_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  received_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  inbound_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  remark VARCHAR(255) NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_purchase_order_no(org_id, order_no)
) COMMENT='采购订单头';

CREATE TABLE IF NOT EXISTS biz_purchase_order_d (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  order_id BIGINT NOT NULL,
  line_no INT NOT NULL,
  req_line_id BIGINT NULL,
  material_id BIGINT NOT NULL,
  order_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  received_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  inbound_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
  remark VARCHAR(255) NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_purchase_order_line(org_id, order_id, line_no),
  KEY idx_purchase_order_d_h(order_id)
) COMMENT='采购订单行';

CREATE TABLE IF NOT EXISTS biz_purchase_receipt_h (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  receipt_no VARCHAR(64) NOT NULL,
  order_id BIGINT NOT NULL,
  biz_date DATE NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'CONFIRMED',
  total_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  inbound_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  qc_enabled TINYINT NOT NULL DEFAULT 0,
  remark VARCHAR(255) NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_purchase_receipt_no(org_id, receipt_no)
) COMMENT='采购收货头';

CREATE TABLE IF NOT EXISTS biz_purchase_receipt_d (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  receipt_id BIGINT NOT NULL,
  line_no INT NOT NULL,
  order_line_id BIGINT NOT NULL,
  material_id BIGINT NOT NULL,
  receipt_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  inbound_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_purchase_receipt_line(org_id, receipt_id, line_no),
  KEY idx_purchase_receipt_d_h(receipt_id)
) COMMENT='采购收货行';

CREATE TABLE IF NOT EXISTS biz_purchase_inbound_h (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  inbound_no VARCHAR(64) NOT NULL,
  receipt_id BIGINT NOT NULL,
  biz_date DATE NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  total_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  qc_required TINYINT NOT NULL DEFAULT 0,
  qc_passed TINYINT NOT NULL DEFAULT 0,
  inspection_hook_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
  remark VARCHAR(255) NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_purchase_inbound_no(org_id, inbound_no)
) COMMENT='采购入库头';

CREATE TABLE IF NOT EXISTS biz_purchase_inbound_d (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  inbound_id BIGINT NOT NULL,
  line_no INT NOT NULL,
  receipt_line_id BIGINT NOT NULL,
  material_id BIGINT NOT NULL,
  inbound_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_purchase_inbound_line(org_id, inbound_id, line_no),
  KEY idx_purchase_inbound_d_h(inbound_id)
) COMMENT='采购入库行';

CREATE TABLE IF NOT EXISTS biz_purchase_return_h (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  return_no VARCHAR(64) NOT NULL,
  supplier_id BIGINT NOT NULL,
  biz_date DATE NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  approval_status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
  total_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  remark VARCHAR(255) NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_purchase_return_no(org_id, return_no)
) COMMENT='采购退货头';

CREATE TABLE IF NOT EXISTS biz_purchase_return_d (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  return_id BIGINT NOT NULL,
  line_no INT NOT NULL,
  material_id BIGINT NOT NULL,
  return_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  remark VARCHAR(255) NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_purchase_return_line(org_id, return_id, line_no),
  KEY idx_purchase_return_d_h(return_id)
) COMMENT='采购退货行';

-- 初始化菜单（兼容已存在场景）
INSERT INTO md_menu(org_id, parent_id, menu_code, menu_name, menu_type, path, component, icon, permission_code, sort_no, status)
VALUES
(1, NULL, 'PURCHASE', '采购管理', 'MENU', NULL, NULL, 'ShoppingCart', NULL, 40, 'ENABLED'),
(1, NULL, 'PURCHASE_REQ', '采购申请', 'MENU', '/purchase/reqs', 'purchase/PurchaseReqListView', 'DocumentAdd', 'purchase:req:list', 41, 'ENABLED'),
(1, NULL, 'PURCHASE_ORDER', '采购订单', 'MENU', '/purchase/orders', 'purchase/PurchaseOrderListView', 'Tickets', 'purchase:order:list', 42, 'ENABLED'),
(1, NULL, 'PURCHASE_RECEIPT', '收货管理', 'MENU', '/purchase/receipts', 'purchase/PurchaseReceiptListView', 'Van', 'purchase:receipt:list', 43, 'ENABLED'),
(1, NULL, 'PURCHASE_INBOUND', '采购入库', 'MENU', '/purchase/inbounds', 'purchase/PurchaseInboundListView', 'Box', 'purchase:inbound:list', 44, 'ENABLED'),
(1, NULL, 'PURCHASE_RETURN', '采购退货', 'MENU', '/purchase/returns', 'purchase/PurchaseReturnListView', 'RefreshLeft', 'purchase:return:list', 45, 'ENABLED')
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path), component=VALUES(component), permission_code=VALUES(permission_code), status=VALUES(status);

INSERT INTO rel_role_menu(org_id, role_id, menu_id)
SELECT 1, 1, m.id FROM md_menu m WHERE m.org_id=1 AND m.menu_code IN ('PURCHASE','PURCHASE_REQ','PURCHASE_ORDER','PURCHASE_RECEIPT','PURCHASE_INBOUND','PURCHASE_RETURN')
AND NOT EXISTS (SELECT 1 FROM rel_role_menu r WHERE r.org_id=1 AND r.role_id=1 AND r.menu_id=m.id);
