-- 第三包：主数据中心（物料/分类/客户/供应商/仓库/库位）

CREATE TABLE IF NOT EXISTS md_material_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  parent_id BIGINT NULL,
  category_code VARCHAR(64) NOT NULL,
  category_name VARCHAR(128) NOT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'ENABLED',
  approval_status VARCHAR(16) NOT NULL DEFAULT 'APPROVED',
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_md_material_category_code(org_id, category_code)
) COMMENT='物料分类';

CREATE TABLE IF NOT EXISTS md_material (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  material_code VARCHAR(64) NOT NULL,
  material_name VARCHAR(128) NOT NULL,
  category_id BIGINT NULL,
  spec VARCHAR(128) NULL,
  model VARCHAR(128) NULL,
  unit VARCHAR(32) NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'DISABLED',
  approval_status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
  submit_time TIMESTAMP NULL,
  audit_time TIMESTAMP NULL,
  audit_by BIGINT NULL,
  audit_remark VARCHAR(255) NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_md_material_code(org_id, material_code),
  KEY idx_md_material_category(category_id)
) COMMENT='物料主数据';

CREATE TABLE IF NOT EXISTS md_customer (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  customer_code VARCHAR(64) NOT NULL,
  customer_name VARCHAR(128) NOT NULL,
  contact_name VARCHAR(64) NULL,
  contact_phone VARCHAR(32) NULL,
  address VARCHAR(255) NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'DISABLED',
  approval_status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
  submit_time TIMESTAMP NULL,
  audit_time TIMESTAMP NULL,
  audit_by BIGINT NULL,
  audit_remark VARCHAR(255) NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_md_customer_code(org_id, customer_code)
) COMMENT='客户主数据';

CREATE TABLE IF NOT EXISTS md_supplier (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  supplier_code VARCHAR(64) NOT NULL,
  supplier_name VARCHAR(128) NOT NULL,
  contact_name VARCHAR(64) NULL,
  contact_phone VARCHAR(32) NULL,
  address VARCHAR(255) NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'DISABLED',
  approval_status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
  submit_time TIMESTAMP NULL,
  audit_time TIMESTAMP NULL,
  audit_by BIGINT NULL,
  audit_remark VARCHAR(255) NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_md_supplier_code(org_id, supplier_code)
) COMMENT='供应商主数据';

CREATE TABLE IF NOT EXISTS md_warehouse (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  warehouse_code VARCHAR(64) NOT NULL,
  warehouse_name VARCHAR(128) NOT NULL,
  warehouse_type VARCHAR(32) NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'DISABLED',
  approval_status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
  submit_time TIMESTAMP NULL,
  audit_time TIMESTAMP NULL,
  audit_by BIGINT NULL,
  audit_remark VARCHAR(255) NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_md_warehouse_code(org_id, warehouse_code)
) COMMENT='仓库主数据';

CREATE TABLE IF NOT EXISTS md_location (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  warehouse_id BIGINT NOT NULL,
  location_code VARCHAR(64) NOT NULL,
  location_name VARCHAR(128) NOT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'DISABLED',
  approval_status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
  submit_time TIMESTAMP NULL,
  audit_time TIMESTAMP NULL,
  audit_by BIGINT NULL,
  audit_remark VARCHAR(255) NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_md_location_code(org_id, warehouse_id, location_code),
  KEY idx_md_location_wh(warehouse_id)
) COMMENT='库位主数据';

INSERT INTO md_material_category(org_id, parent_id, category_code, category_name, status, approval_status)
VALUES (1, NULL, 'RAW', '原料', 'ENABLED', 'APPROVED'),
       (1, NULL, 'FG', '成品', 'ENABLED', 'APPROVED')
ON DUPLICATE KEY UPDATE category_name=VALUES(category_name), status=VALUES(status), approval_status=VALUES(approval_status);

INSERT INTO md_material(org_id, material_code, material_name, category_id, spec, model, unit, status, approval_status)
SELECT 1, 'MAT001', '示例物料A', c.id, '10x10', 'A1', 'PCS', 'ENABLED', 'APPROVED'
FROM md_material_category c WHERE c.org_id=1 AND c.category_code='RAW'
ON DUPLICATE KEY UPDATE material_name=VALUES(material_name), status=VALUES(status), approval_status=VALUES(approval_status);

INSERT INTO md_customer(org_id, customer_code, customer_name, contact_name, contact_phone, status, approval_status)
VALUES (1, 'CUS001', '示例客户A', '张三', '13800000001', 'ENABLED', 'APPROVED')
ON DUPLICATE KEY UPDATE customer_name=VALUES(customer_name), status=VALUES(status), approval_status=VALUES(approval_status);

INSERT INTO md_supplier(org_id, supplier_code, supplier_name, contact_name, contact_phone, status, approval_status)
VALUES (1, 'SUP001', '示例供应商A', '李四', '13800000002', 'ENABLED', 'APPROVED')
ON DUPLICATE KEY UPDATE supplier_name=VALUES(supplier_name), status=VALUES(status), approval_status=VALUES(approval_status);

INSERT INTO md_warehouse(org_id, warehouse_code, warehouse_name, warehouse_type, status, approval_status)
VALUES (1, 'WH001', '主仓', 'FG', 'ENABLED', 'APPROVED')
ON DUPLICATE KEY UPDATE warehouse_name=VALUES(warehouse_name), status=VALUES(status), approval_status=VALUES(approval_status);

INSERT INTO md_location(org_id, warehouse_id, location_code, location_name, status, approval_status)
SELECT 1, w.id, 'A-01-01', 'A区01货位', 'ENABLED', 'APPROVED' FROM md_warehouse w WHERE w.org_id=1 AND w.warehouse_code='WH001'
ON DUPLICATE KEY UPDATE location_name=VALUES(location_name), status=VALUES(status), approval_status=VALUES(approval_status);
