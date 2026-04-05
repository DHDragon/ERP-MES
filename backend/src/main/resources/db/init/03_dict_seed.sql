-- 基础字典初始化（示例）
INSERT INTO sys_dict(org_id, dict_code, dict_name, status)
VALUES (1, 'ENABLE_STATUS', '启用状态', 'ENABLED')
ON DUPLICATE KEY UPDATE dict_name=VALUES(dict_name);

INSERT INTO sys_dict_item(org_id, dict_id, item_code, item_name, sort_no, status)
SELECT 1, d.id, 'ENABLED', '启用', 1, 'ENABLED' FROM sys_dict d WHERE d.org_id=1 AND d.dict_code='ENABLE_STATUS'
ON DUPLICATE KEY UPDATE item_name=VALUES(item_name), status=VALUES(status);

INSERT INTO sys_dict_item(org_id, dict_id, item_code, item_name, sort_no, status)
SELECT 1, d.id, 'DISABLED', '停用', 2, 'ENABLED' FROM sys_dict d WHERE d.org_id=1 AND d.dict_code='ENABLE_STATUS'
ON DUPLICATE KEY UPDATE item_name=VALUES(item_name), status=VALUES(status);
