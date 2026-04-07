-- 第六包：库存基础层

CREATE TABLE IF NOT EXISTS biz_inventory_stock (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  warehouse_id BIGINT NOT NULL,
  location_id BIGINT NULL,
  material_id BIGINT NOT NULL,
  batch_no VARCHAR(64) NULL,
  serial_no VARCHAR(64) NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'AVAILABLE',
  qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  locked_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uq_stock(org_id, warehouse_id, location_id, material_id, batch_no, serial_no, status),
  KEY idx_stock_material(org_id, material_id),
  KEY idx_stock_warehouse(org_id, warehouse_id)
) COMMENT='即时库存表';

CREATE TABLE IF NOT EXISTS biz_inventory_ledger (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  biz_type VARCHAR(32) NOT NULL,
  biz_id BIGINT NOT NULL,
  biz_line_id BIGINT NULL,
  warehouse_id BIGINT NOT NULL,
  location_id BIGINT NULL,
  material_id BIGINT NOT NULL,
  batch_no VARCHAR(64) NULL,
  serial_no VARCHAR(64) NULL,
  status VARCHAR(32) NOT NULL,
  qty_before DECIMAL(18,6) NOT NULL DEFAULT 0,
  qty_change DECIMAL(18,6) NOT NULL,
  qty_after DECIMAL(18,6) NOT NULL DEFAULT 0,
  direction VARCHAR(8) NOT NULL,
  remark VARCHAR(255) NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_ledger_biz(org_id, biz_type, biz_id),
  KEY idx_ledger_material(org_id, material_id),
  KEY idx_ledger_warehouse(org_id, warehouse_id)
) COMMENT='库存台账表';

CREATE TABLE IF NOT EXISTS biz_transfer_order_h (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  transfer_no VARCHAR(64) NOT NULL,
  from_warehouse_id BIGINT NOT NULL,
  to_warehouse_id BIGINT NOT NULL,
  biz_date DATE NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  approval_status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
  total_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  executed_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  remark VARCHAR(255) NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_transfer_no(org_id, transfer_no)
) COMMENT='调拨单头';

CREATE TABLE IF NOT EXISTS biz_transfer_order_d (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  transfer_id BIGINT NOT NULL,
  line_no INT NOT NULL,
  material_id BIGINT NOT NULL,
  batch_no VARCHAR(64) NULL,
  serial_no VARCHAR(64) NULL,
  from_location_id BIGINT NULL,
  to_location_id BIGINT NULL,
  qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  executed_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_transfer_line(org_id, transfer_id, line_no),
  KEY idx_transfer_d_h(transfer_id)
) COMMENT='调拨单行';

CREATE TABLE IF NOT EXISTS biz_count_order_h (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  count_no VARCHAR(64) NOT NULL,
  warehouse_id BIGINT NOT NULL,
  biz_date DATE NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  approval_status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
  total_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  diff_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  remark VARCHAR(255) NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_count_no(org_id, count_no)
) COMMENT='盘点单头';

CREATE TABLE IF NOT EXISTS biz_count_order_d (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  count_id BIGINT NOT NULL,
  line_no INT NOT NULL,
  material_id BIGINT NOT NULL,
  batch_no VARCHAR(64) NULL,
  serial_no VARCHAR(64) NULL,
  location_id BIGINT NULL,
  qty_book DECIMAL(18,6) NOT NULL DEFAULT 0,
  qty_actual DECIMAL(18,6) NOT NULL DEFAULT 0,
  diff_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_count_line(org_id, count_id, line_no),
  KEY idx_count_d_h(count_id)
) COMMENT='盘点单行';

-- 初始化菜单
INSERT INTO md_menu(org_id, parent_id, menu_code, menu_name, menu_type, path, component, icon, permission_code, sort_no, status)
VALUES
(1, NULL, 'INVENTORY', '库存管理', 'MENU', NULL, NULL, 'Box', NULL, 50, 'ENABLED'),
(1, NULL, 'INVENTORY_STOCK', '即时库存', 'MENU', '/inventory/stock', 'inventory/StockQueryView', 'List', 'inventory:stock:query', 51, 'ENABLED'),
(1, NULL, 'INVENTORY_LEDGER', '库存台账', 'MENU', '/inventory/ledger', 'inventory/LedgerQueryView', 'Document', 'inventory:ledger:query', 52, 'ENABLED'),
(1, NULL, 'INVENTORY_TRANSFER', '调拨单', 'MENU', '/inventory/transfers', 'inventory/TransferListView', 'Van', 'inventory:transfer:list', 53, 'ENABLED'),
(1, NULL, 'INVENTORY_COUNT', '盘点单', 'MENU', '/inventory/counts', 'inventory/CountListView', 'Edit', 'inventory:count:list', 54, 'ENABLED')
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path), component=VALUES(component), permission_code=VALUES(permission_code), status=VALUES(status);

INSERT INTO rel_role_menu(org_id, role_id, menu_id)
SELECT 1, 1, m.id FROM md_menu m WHERE m.org_id=1 AND m.menu_code IN ('INVENTORY','INVENTORY_STOCK','INVENTORY_LEDGER','INVENTORY_TRANSFER','INVENTORY_COUNT')
AND NOT EXISTS (SELECT 1 FROM rel_role_menu r WHERE r.org_id=1 AND r.role_id=1 AND r.menu_id=m.id);
