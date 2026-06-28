-- 本次变更：补齐中间表缺失的审计字段 + 新增 client:add 权限
-- 执行时间：2026-06-28
-- 适用场景：已存在的历史数据库（从旧版本升级）
-- 注意：如果某列已存在，对应语句会报错，请根据实际情况跳过

ALTER TABLE sys_permission
    ADD COLUMN create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    ADD COLUMN update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

ALTER TABLE sys_user_role
    ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    ADD COLUMN create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    ADD COLUMN update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

ALTER TABLE sys_role_permission
    ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    ADD COLUMN create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    ADD COLUMN update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- 新增 client:add 权限，供项目录入时"现场新建客户"使用
INSERT IGNORE INTO sys_permission (id, permission_code, permission_name, parent_id, type, path, sort_order)
VALUES (25, 'client:add', '新建委托人/当事人', 7, 'button', 'POST:/api/clients', 4);

-- 给财务、律师补上该权限（管理员已有全部权限）
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT 2, id FROM sys_permission WHERE permission_code = 'client:add';

INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT 4, id FROM sys_permission WHERE permission_code = 'client:add';

-- 客户表新增身份证号解析出的出生日期与性别
ALTER TABLE client
    ADD COLUMN birth_date DATE COMMENT '出生日期（从身份证号解析）' AFTER id_card,
    ADD COLUMN gender VARCHAR(4) COMMENT '性别（从身份证号解析）' AFTER birth_date;
