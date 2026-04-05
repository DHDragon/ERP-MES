-- 基础菜单初始化（示例）
INSERT INTO sys_menu(org_id, parent_id, menu_code, menu_name, path, component, icon, sort_no, menu_type, status)
VALUES
(1, NULL, 'HOME', '首页', '/', 'HomeView', 'House', 1, 'MENU', 'ENABLED'),
(1, NULL, 'BASE', '基础数据', NULL, NULL, 'Setting', 10, 'MENU', 'ENABLED'),
(1, (SELECT id FROM sys_menu WHERE org_id=1 AND menu_code='BASE' LIMIT 1), 'BASE_USER', '用户管理', '/base/users', 'UsersView', 'User', 11, 'MENU', 'ENABLED'),
(1, (SELECT id FROM sys_menu WHERE org_id=1 AND menu_code='BASE' LIMIT 1), 'BASE_MENU', '菜单管理', '/base/menus', 'MenusView', 'Menu', 12, 'MENU', 'ENABLED')
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path), component=VALUES(component), status=VALUES(status);
