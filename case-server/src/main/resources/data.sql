-- 初始化角色
INSERT IGNORE INTO sys_role (role_code, role_name, description) VALUES
('admin', '超级管理员', '系统所有权限'),
('finance', '财务人员', '管理案件、收入、支出、社保、个税、档案费'),
('staff', '行政人员', '基础数据维护'),
('lawyer', '律师', '查看本人数据、录入项目信息');

-- 初始化权限
INSERT IGNORE INTO sys_permission (id, permission_code, permission_name, parent_id, type, path, sort_order) VALUES
(1, 'system', '系统管理', 0, 'menu', '', 1),
(2, 'system:user', '用户管理', 1, 'menu', '/system/user', 1),
(3, 'system:user:add', '新增用户', 2, 'button', 'POST:/api/users', 1),
(4, 'system:user:edit', '编辑用户', 2, 'button', 'PUT:/api/users/*', 2),
(5, 'system:user:delete', '删除用户', 2, 'button', 'DELETE:/api/users/*', 3),
(6, 'system:role', '角色管理', 1, 'menu', '/system/role', 2),
(7, 'project', '项目管理', 0, 'menu', '/project', 2),
(8, 'project:list', '项目列表', 7, 'menu', '/project/list', 1),
(9, 'project:add', '新增项目', 7, 'button', 'POST:/api/projects', 2),
(10, 'project:edit', '编辑项目', 7, 'button', 'PUT:/api/projects/*', 3),
(11, 'income', '收入管理', 0, 'menu', '/income', 3),
(12, 'income:add', '登记收入', 11, 'button', 'POST:/api/incomes', 1),
(13, 'ledger', '台账管理', 0, 'menu', '/ledger', 4),
(14, 'ledger:view', '查看台账', 13, 'menu', '/ledger/account', 1),
(15, 'archive', '档案费', 0, 'menu', '/archive/list', 5),
(16, 'social', '社保公积金', 0, 'menu', '/social', 6),
(17, 'social:config', '配置管理', 16, 'menu', '/social/config', 1),
(18, 'social:record', '扣缴记录', 16, 'menu', '/social/record', 2),
(19, 'report', '报表统计', 0, 'menu', '/report', 7),
(20, 'tax', '个税管理', 0, 'menu', '/tax', 8),
(21, 'tax:record', '个税扣缴', 20, 'menu', '/tax/record', 1),
(22, 'import', '数据导入', 0, 'menu', '/import', 9),
(23, 'expense', '支出管理', 0, 'menu', '/expense/list', 10),
(24, 'expense:add', '新增支出', 23, 'button', 'POST:/api/expenses', 1);

-- 初始化管理员账号，密码为 admin123（BCrypt 加密）
INSERT IGNORE INTO sys_user (username, password, real_name, phone, status) VALUES
('admin', '$2a$10$p2T801B8.3OxCkYndmkXoe1FHyal7m21BmhAY4rK6D1SiBFuHqLte', '系统管理员', '13800000000', 1);

-- 关联管理员角色
INSERT IGNORE INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 给超级管理员赋权所有权限
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- 给财务人员赋权
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT 2, id FROM sys_permission WHERE permission_code LIKE 'project:%' OR permission_code LIKE 'income:%' OR permission_code LIKE 'ledger:%' OR permission_code LIKE 'archive:%' OR permission_code LIKE 'social:%' OR permission_code LIKE 'report:%' OR permission_code LIKE 'tax:%' OR permission_code LIKE 'expense:%' OR permission_code = 'import';

-- 给律师赋权
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT 4, id FROM sys_permission WHERE permission_code IN ('project:list','project:add','income:add','ledger:view');

-- 系统配置
INSERT IGNORE INTO sys_config (config_key, config_value, description) VALUES
('ledger.default.commission.rate', '0.85', '默认提成比例'),
('ledger.tax.threshold', '5000', '个税起征点'),
('project.no.prefix', 'MS', '项目编号前缀');

-- 初始化律师
INSERT IGNORE INTO lawyer (id, user_id, name, employee_no, phone, commission_rate, opening_balance, status) VALUES
(1, 1, '闫国强', 'L001', '13800000001', 0.85, 0.00, 1),
(2, 1, '张耀华', 'L002', '13800000002', 0.85, 0.00, 1),
(3, 1, '白一冰', 'L003', '13800000003', 0.85, 0.00, 1);

-- 初始化委托人/当事人
INSERT IGNORE INTO client (id, name, client_type, phone) VALUES
(1, '张三', 1, '13900000001'),
(2, '李四', 1, '13900000002'),
(3, '王五', 2, '13900000003');
