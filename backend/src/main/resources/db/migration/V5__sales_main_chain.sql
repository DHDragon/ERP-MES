-- 第四包：销售主链路（销售订单 -> 发货通知 -> 销售出库）

CREATE TABLE IF NOT EXISTS biz_sales_order_h (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  order_no VARCHAR(64) NOT NULL,
  customer_id BIGINT NOT NULL,
  biz_date DATE NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  approval_status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
  total_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  delivered_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  remark VARCHAR(255) NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_sales_order_no(org_id, order_no)
) COMMENT='销售订单头';

CREATE TABLE IF NOT EXISTS biz_sales_order_d (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  order_id BIGINT NOT NULL,
  line_no INT NOT NULL,
  material_id BIGINT NOT NULL,
  order_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  delivered_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
  remark VARCHAR(255) NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_sales_order_line(org_id, order_id, line_no),
  KEY idx_sales_order_d_h(order_id)
) COMMENT='销售订单行';

CREATE TABLE IF NOT EXISTS biz_delivery_notice_h (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  notice_no VARCHAR(64) NOT NULL,
  order_id BIGINT NOT NULL,
  biz_date DATE NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  total_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  outbound_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  remark VARCHAR(255) NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_delivery_notice_no(org_id, notice_no)
) COMMENT='发货通知头';

CREATE TABLE IF NOT EXISTS biz_delivery_notice_d (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  notice_id BIGINT NOT NULL,
  line_no INT NOT NULL,
  order_line_id BIGINT NOT NULL,
  material_id BIGINT NOT NULL,
  notice_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  outbound_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_delivery_notice_line(org_id, notice_id, line_no),
  KEY idx_delivery_notice_d_h(notice_id)
) COMMENT='发货通知行';

CREATE TABLE IF NOT EXISTS biz_sales_outbound_h (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  outbound_no VARCHAR(64) NOT NULL,
  notice_id BIGINT NOT NULL,
  biz_date DATE NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  total_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  pda_record_json TEXT NULL,
  remark VARCHAR(255) NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_sales_outbound_no(org_id, outbound_no)
) COMMENT='销售出库头';

CREATE TABLE IF NOT EXISTS biz_sales_outbound_d (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  outbound_id BIGINT NOT NULL,
  line_no INT NOT NULL,
  notice_line_id BIGINT NOT NULL,
  material_id BIGINT NOT NULL,
  outbound_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_sales_outbound_line(org_id, outbound_id, line_no),
  KEY idx_sales_outbound_d_h(outbound_id)
) COMMENT='销售出库行';

CREATE TABLE IF NOT EXISTS biz_sales_return_h (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  return_no VARCHAR(64) NOT NULL,
  customer_id BIGINT NOT NULL,
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
  UNIQUE KEY uq_sales_return_no(org_id, return_no)
) COMMENT='销售退货头';

CREATE TABLE IF NOT EXISTS biz_sales_return_d (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  return_id BIGINT NOT NULL,
  line_no INT NOT NULL,
  material_id BIGINT NOT NULL,
  return_qty DECIMAL(18,6) NOT NULL DEFAULT 0,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_sales_return_line(org_id, return_id, line_no),
  KEY idx_sales_return_d_h(return_id)
) COMMENT='销售退货行';

-- 初始化菜单（兼容已存在场景）
INSERT INTO md_menu(org_id, parent_id, menu_code, menu_name, menu_type, path, component, icon, permission_code, sort_no, status)
VALUES
(1, NULL, 'SALES', '销售管理', 'MENU', NULL, NULL, 'Tickets', NULL, 30, 'ENABLED'),
(1, NULL, 'SALES_ORDER', '销售订单', 'MENU', '/sales/orders', 'sales/SalesOrderListView', 'Document', 'sales:order:list', 31, 'ENABLED'),
(1, NULL, 'DELIVERY_NOTICE', '发货通知', 'MENU', '/sales/delivery-notices', 'sales/DeliveryNoticeListView', 'Van', 'sales:notice:list', 32, 'ENABLED'),
(1, NULL, 'SALES_OUTBOUND', '销售出库', 'MENU', '/sales/outbounds', 'sales/SalesOutboundListView', 'Box', 'sales:outbound:list', 33, 'ENABLED'),
(1, NULL, 'SALES_RETURN', '销售退货', 'MENU', '/sales/returns', 'sales/SalesReturnListView', 'RefreshLeft', 'sales:return:list', 34, 'ENABLED')
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path), component=VALUES(component), permission_code=VALUES(permission_code), status=VALUES(status);

INSERT INTO rel_role_menu(org_id, role_id, menu_id)
SELECT 1, 1, m.id FROM md_menu m WHERE m.org_id=1 AND m.menu_code IN ('SALES','SALES_ORDER','DELIVERY_NOTICE','SALES_OUTBOUND','SALES_RETURN')
AND NOT EXISTS (SELECT 1 FROM rel_role_menu r WHERE r.org_id=1 AND r.role_id=1 AND r.menu_id=m.id);
