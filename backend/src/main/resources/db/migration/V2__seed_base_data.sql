-- 基础数据初始化（管理员/菜单/字典）

-- admin/admin123：此处 password_hash 为演示用明文占位，后续替换为 BCrypt
INSERT INTO sys_org(id, org_code, org_name, status) VALUES (1, 'DEFAULT', '默认组织', 'ENABLED')
ON DUPLICATE KEY UPDATE org_name=VALUES(org_name);

INSERT INTO sys_user(id, org_id, username, password_hash, real_name, status)
VALUES (1, 1, 'admin', 'admin123', '系统管理员', 'ENABLED')
ON DUPLICATE KEY UPDATE real_name=VALUES(real_name);

INSERT INTO sys_role(id, org_id, role_code, role_name, status)
VALUES (1, 1, 'ADMIN', '管理员', 'ENABLED')
ON DUPLICATE KEY UPDATE role_name=VALUES(role_name);

INSERT INTO sys_user_role(org_id, user_id, role_id)
SELECT 1, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_user_role WHERE org_id=1 AND user_id=1 AND role_id=1);

-- 菜单（示例）
INSERT INTO sys_menu(org_id, parent_id, menu_code, menu_name, path, component, icon, sort_no, menu_type, status)
VALUES
(1, NULL, 'HOME', '首页', '/', 'HomeView', 'House', 1, 'MENU', 'ENABLED'),
(1, NULL, 'BASE', '基础数据', NULL, NULL, 'Setting', 10, 'MENU', 'ENABLED'),
(1, (SELECT id FROM sys_menu WHERE org_id=1 AND menu_code='BASE' LIMIT 1), 'BASE_USER', '用户管理', '/base/users', 'UsersView', 'User', 11, 'MENU', 'ENABLED'),
(1, (SELECT id FROM sys_menu WHERE org_id=1 AND menu_code='BASE' LIMIT 1), 'BASE_MENU', '菜单管理', '/base/menus', 'MenusView', 'Menu', 12, 'MENU', 'ENABLED')
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path), component=VALUES(component), status=VALUES(status);

-- 字典（示例）
INSERT INTO sys_dict(org_id, dict_code, dict_name, status)
VALUES (1, 'ENABLE_STATUS', '启用状态', 'ENABLED')
ON DUPLICATE KEY UPDATE dict_name=VALUES(dict_name);

INSERT INTO sys_dict_item(org_id, dict_id, item_code, item_name, sort_no, status)
SELECT 1, d.id, 'ENABLED', '启用', 1, 'ENABLED' FROM sys_dict d WHERE d.org_id=1 AND d.dict_code='ENABLE_STATUS'
ON DUPLICATE KEY UPDATE item_name=VALUES(item_name), status=VALUES(status);

INSERT INTO sys_dict_item(org_id, dict_id, item_code, item_name, sort_no, status)
SELECT 1, d.id, 'DISABLED', '停用', 2, 'ENABLED' FROM sys_dict d WHERE d.org_id=1 AND d.dict_code='ENABLE_STATUS'
ON DUPLICATE KEY UPDATE item_name=VALUES(item_name), status=VALUES(status);
