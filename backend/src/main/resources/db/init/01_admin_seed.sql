-- 基础管理员账户初始化（示例）
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
