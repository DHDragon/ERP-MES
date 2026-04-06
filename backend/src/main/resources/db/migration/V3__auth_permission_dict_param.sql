-- 第二包：登录、权限、菜单、字典、参数中心

CREATE TABLE IF NOT EXISTS md_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  org_id BIGINT NOT NULL COMMENT '组织ID',
  dept_id BIGINT NULL COMMENT '部门ID',
  username VARCHAR(64) NOT NULL COMMENT '用户名',
  password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
  real_name VARCHAR(64) NULL COMMENT '姓名',
  mobile VARCHAR(32) NULL COMMENT '手机号',
  email VARCHAR(128) NULL COMMENT '邮箱',
  status VARCHAR(16) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_md_user_username(org_id, username),
  KEY idx_md_user_org(org_id),
  KEY idx_md_user_dept(dept_id)
) COMMENT='用户主数据';

CREATE TABLE IF NOT EXISTS md_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  org_id BIGINT NOT NULL COMMENT '组织ID',
  role_code VARCHAR(64) NOT NULL COMMENT '角色编码',
  role_name VARCHAR(128) NOT NULL COMMENT '角色名称',
  status VARCHAR(16) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_md_role_code(org_id, role_code)
) COMMENT='角色主数据';

CREATE TABLE IF NOT EXISTS md_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  org_id BIGINT NOT NULL COMMENT '组织ID',
  parent_id BIGINT NULL COMMENT '父菜单ID',
  menu_code VARCHAR(64) NOT NULL COMMENT '菜单编码',
  menu_name VARCHAR(128) NOT NULL COMMENT '菜单名称',
  menu_type VARCHAR(16) NOT NULL DEFAULT 'MENU' COMMENT 'MENU/BUTTON',
  path VARCHAR(255) NULL COMMENT '路由路径',
  component VARCHAR(255) NULL COMMENT '组件',
  icon VARCHAR(64) NULL COMMENT '图标',
  permission_code VARCHAR(128) NULL COMMENT '权限点编码',
  sort_no INT NOT NULL DEFAULT 0 COMMENT '排序',
  status VARCHAR(16) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_md_menu_code(org_id, menu_code),
  KEY idx_md_menu_parent(parent_id)
) COMMENT='菜单主数据';

CREATE TABLE IF NOT EXISTS rel_user_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_rel_user_role(org_id, user_id, role_id)
) COMMENT='用户角色关系';

CREATE TABLE IF NOT EXISTS rel_role_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  menu_id BIGINT NOT NULL,
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_rel_role_menu(org_id, role_id, menu_id)
) COMMENT='角色菜单关系';

CREATE TABLE IF NOT EXISTS cfg_data_scope (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL COMMENT '角色ID',
  scope_type VARCHAR(32) NOT NULL COMMENT 'SELF/DEPT/ORG/ORG_LIST/ALL',
  scope_org_ids VARCHAR(1024) NULL COMMENT '指定组织ID逗号分隔',
  status VARCHAR(16) NOT NULL DEFAULT 'ENABLED',
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_cfg_data_scope(org_id, role_id)
) COMMENT='数据权限配置';

CREATE TABLE IF NOT EXISTS cfg_system_param (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  param_key VARCHAR(64) NOT NULL,
  param_name VARCHAR(128) NOT NULL,
  param_value VARCHAR(1024) NOT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'ENABLED',
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_cfg_system_param(org_id, param_key)
) COMMENT='系统参数';

CREATE TABLE IF NOT EXISTS md_dict_type (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  dict_type VARCHAR(64) NOT NULL,
  dict_name VARCHAR(128) NOT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'ENABLED',
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_md_dict_type(org_id, dict_type)
) COMMENT='字典类型';

CREATE TABLE IF NOT EXISTS md_dict_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  dict_type_id BIGINT NOT NULL,
  item_code VARCHAR(64) NOT NULL,
  item_name VARCHAR(128) NOT NULL,
  item_sort INT NOT NULL DEFAULT 0,
  status VARCHAR(16) NOT NULL DEFAULT 'ENABLED',
  created_by BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_md_dict_item(org_id, dict_type_id, item_code)
) COMMENT='字典项';

-- 初始化账号/角色/菜单/权限
INSERT INTO md_user(id, org_id, username, password_hash, real_name, status)
VALUES (1, 1, 'admin', 'admin123', '系统管理员', 'ENABLED')
ON DUPLICATE KEY UPDATE real_name=VALUES(real_name), status=VALUES(status);

INSERT INTO md_role(id, org_id, role_code, role_name, status)
VALUES (1, 1, 'ADMIN', '管理员', 'ENABLED')
ON DUPLICATE KEY UPDATE role_name=VALUES(role_name), status=VALUES(status);

INSERT INTO rel_user_role(org_id, user_id, role_id)
SELECT 1, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM rel_user_role WHERE org_id=1 AND user_id=1 AND role_id=1);

INSERT INTO md_menu(id, org_id, parent_id, menu_code, menu_name, menu_type, path, component, icon, permission_code, sort_no, status)
VALUES
(100,1,NULL,'HOME','首页','MENU','/','HomeView','House',NULL,1,'ENABLED'),
(200,1,NULL,'SYSTEM','系统设置','MENU',NULL,NULL,'Setting',NULL,10,'ENABLED'),
(210,1,200,'SYSTEM_USER','用户管理','MENU','/base/users','UsersManageView','User','sys:user:list',11,'ENABLED'),
(211,1,210,'SYSTEM_USER_ADD','用户新增','BUTTON',NULL,NULL,NULL,'sys:user:add',111,'ENABLED'),
(212,1,210,'SYSTEM_USER_EDIT','用户编辑','BUTTON',NULL,NULL,NULL,'sys:user:edit',112,'ENABLED'),
(213,1,210,'SYSTEM_USER_DEL','用户删除','BUTTON',NULL,NULL,NULL,'sys:user:delete',113,'ENABLED'),
(220,1,200,'SYSTEM_ROLE','角色管理','MENU','/base/roles','RolesManageView','Avatar','sys:role:list',12,'ENABLED'),
(221,1,220,'SYSTEM_ROLE_EDIT','角色编辑','BUTTON',NULL,NULL,NULL,'sys:role:edit',121,'ENABLED'),
(230,1,200,'SYSTEM_MENU','菜单管理','MENU','/base/menus','MenusManageView','Menu','sys:menu:list',13,'ENABLED'),
(231,1,230,'SYSTEM_MENU_EDIT','菜单编辑','BUTTON',NULL,NULL,NULL,'sys:menu:edit',131,'ENABLED'),
(240,1,200,'SYSTEM_DICT','字典管理','MENU','/base/dicts','DictManageView','Collection','sys:dict:list',14,'ENABLED'),
(241,1,240,'SYSTEM_DICT_EDIT','字典编辑','BUTTON',NULL,NULL,NULL,'sys:dict:edit',141,'ENABLED'),
(250,1,200,'SYSTEM_PARAM','参数中心','MENU','/base/params','ParamCenterView','Tools','sys:param:list',15,'ENABLED'),
(251,1,250,'SYSTEM_PARAM_EDIT','参数编辑','BUTTON',NULL,NULL,NULL,'sys:param:edit',151,'ENABLED')
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path), component=VALUES(component), permission_code=VALUES(permission_code), status=VALUES(status);

INSERT INTO rel_role_menu(org_id, role_id, menu_id)
SELECT 1, 1, m.id FROM md_menu m WHERE m.org_id=1
AND NOT EXISTS (SELECT 1 FROM rel_role_menu r WHERE r.org_id=1 AND r.role_id=1 AND r.menu_id=m.id);

INSERT INTO cfg_data_scope(org_id, role_id, scope_type, status)
VALUES (1, 1, 'ALL', 'ENABLED')
ON DUPLICATE KEY UPDATE scope_type=VALUES(scope_type), status=VALUES(status);

INSERT INTO cfg_system_param(org_id, param_key, param_name, param_value, status)
VALUES
(1,'DEFAULT_PAGE_SIZE','默认分页大小','20','ENABLED'),
(1,'UPLOAD_MAX_MB','上传大小上限MB','20','ENABLED')
ON DUPLICATE KEY UPDATE param_name=VALUES(param_name), param_value=VALUES(param_value), status=VALUES(status);

-- 初始化字典类型
INSERT INTO md_dict_type(org_id, dict_type, dict_name, status)
VALUES
(1,'STATUS','状态','ENABLED'),
(1,'APPROVAL_STATUS','审批状态','ENABLED'),
(1,'UNIT','单位','ENABLED'),
(1,'ORG_TYPE','组织类型','ENABLED'),
(1,'WAREHOUSE_TYPE','仓库类型','ENABLED'),
(1,'INSPECTION_METHOD','检验方式','ENABLED'),
(1,'ALERT_TYPE','预警类型','ENABLED')
ON DUPLICATE KEY UPDATE dict_name=VALUES(dict_name), status=VALUES(status);

-- 初始化字典项
INSERT INTO md_dict_item(org_id, dict_type_id, item_code, item_name, item_sort, status)
SELECT 1, t.id, 'ENABLED', '启用', 1, 'ENABLED' FROM md_dict_type t WHERE t.org_id=1 AND t.dict_type='STATUS'
ON DUPLICATE KEY UPDATE item_name=VALUES(item_name), status=VALUES(status);
INSERT INTO md_dict_item(org_id, dict_type_id, item_code, item_name, item_sort, status)
SELECT 1, t.id, 'DISABLED', '停用', 2, 'ENABLED' FROM md_dict_type t WHERE t.org_id=1 AND t.dict_type='STATUS'
ON DUPLICATE KEY UPDATE item_name=VALUES(item_name), status=VALUES(status);

INSERT INTO md_dict_item(org_id, dict_type_id, item_code, item_name, item_sort, status)
SELECT 1, t.id, 'PENDING', '待审批', 1, 'ENABLED' FROM md_dict_type t WHERE t.org_id=1 AND t.dict_type='APPROVAL_STATUS'
ON DUPLICATE KEY UPDATE item_name=VALUES(item_name), status=VALUES(status);
INSERT INTO md_dict_item(org_id, dict_type_id, item_code, item_name, item_sort, status)
SELECT 1, t.id, 'APPROVED', '已审批', 2, 'ENABLED' FROM md_dict_type t WHERE t.org_id=1 AND t.dict_type='APPROVAL_STATUS'
ON DUPLICATE KEY UPDATE item_name=VALUES(item_name), status=VALUES(status);
INSERT INTO md_dict_item(org_id, dict_type_id, item_code, item_name, item_sort, status)
SELECT 1, t.id, 'REJECTED', '已驳回', 3, 'ENABLED' FROM md_dict_type t WHERE t.org_id=1 AND t.dict_type='APPROVAL_STATUS'
ON DUPLICATE KEY UPDATE item_name=VALUES(item_name), status=VALUES(status);

INSERT INTO md_dict_item(org_id, dict_type_id, item_code, item_name, item_sort, status)
SELECT 1, t.id, 'PCS', '件', 1, 'ENABLED' FROM md_dict_type t WHERE t.org_id=1 AND t.dict_type='UNIT'
ON DUPLICATE KEY UPDATE item_name=VALUES(item_name), status=VALUES(status);
INSERT INTO md_dict_item(org_id, dict_type_id, item_code, item_name, item_sort, status)
SELECT 1, t.id, 'KG', '千克', 2, 'ENABLED' FROM md_dict_type t WHERE t.org_id=1 AND t.dict_type='UNIT'
ON DUPLICATE KEY UPDATE item_name=VALUES(item_name), status=VALUES(status);

INSERT INTO md_dict_item(org_id, dict_type_id, item_code, item_name, item_sort, status)
SELECT 1, t.id, 'FACTORY', '工厂', 1, 'ENABLED' FROM md_dict_type t WHERE t.org_id=1 AND t.dict_type='ORG_TYPE'
ON DUPLICATE KEY UPDATE item_name=VALUES(item_name), status=VALUES(status);
INSERT INTO md_dict_item(org_id, dict_type_id, item_code, item_name, item_sort, status)
SELECT 1, t.id, 'WAREHOUSE', '仓库组织', 2, 'ENABLED' FROM md_dict_type t WHERE t.org_id=1 AND t.dict_type='ORG_TYPE'
ON DUPLICATE KEY UPDATE item_name=VALUES(item_name), status=VALUES(status);

INSERT INTO md_dict_item(org_id, dict_type_id, item_code, item_name, item_sort, status)
SELECT 1, t.id, 'RAW', '原料仓', 1, 'ENABLED' FROM md_dict_type t WHERE t.org_id=1 AND t.dict_type='WAREHOUSE_TYPE'
ON DUPLICATE KEY UPDATE item_name=VALUES(item_name), status=VALUES(status);
INSERT INTO md_dict_item(org_id, dict_type_id, item_code, item_name, item_sort, status)
SELECT 1, t.id, 'FG', '成品仓', 2, 'ENABLED' FROM md_dict_type t WHERE t.org_id=1 AND t.dict_type='WAREHOUSE_TYPE'
ON DUPLICATE KEY UPDATE item_name=VALUES(item_name), status=VALUES(status);

INSERT INTO md_dict_item(org_id, dict_type_id, item_code, item_name, item_sort, status)
SELECT 1, t.id, 'FULL', '全检', 1, 'ENABLED' FROM md_dict_type t WHERE t.org_id=1 AND t.dict_type='INSPECTION_METHOD'
ON DUPLICATE KEY UPDATE item_name=VALUES(item_name), status=VALUES(status);
INSERT INTO md_dict_item(org_id, dict_type_id, item_code, item_name, item_sort, status)
SELECT 1, t.id, 'SAMPLE', '抽检', 2, 'ENABLED' FROM md_dict_type t WHERE t.org_id=1 AND t.dict_type='INSPECTION_METHOD'
ON DUPLICATE KEY UPDATE item_name=VALUES(item_name), status=VALUES(status);

INSERT INTO md_dict_item(org_id, dict_type_id, item_code, item_name, item_sort, status)
SELECT 1, t.id, 'INVENTORY_LOW', '库存预警', 1, 'ENABLED' FROM md_dict_type t WHERE t.org_id=1 AND t.dict_type='ALERT_TYPE'
ON DUPLICATE KEY UPDATE item_name=VALUES(item_name), status=VALUES(status);
INSERT INTO md_dict_item(org_id, dict_type_id, item_code, item_name, item_sort, status)
SELECT 1, t.id, 'QUALITY_NG', '质量预警', 2, 'ENABLED' FROM md_dict_type t WHERE t.org_id=1 AND t.dict_type='ALERT_TYPE'
ON DUPLICATE KEY UPDATE item_name=VALUES(item_name), status=VALUES(status);
