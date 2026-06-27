-- MySQL schema

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    username VARCHAR(64) NOT NULL UNIQUE COMMENT '登录账号',
    password VARCHAR(128) NOT NULL COMMENT '密码',
    real_name VARCHAR(64) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态 1正常 0禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    role_code VARCHAR(64) NOT NULL UNIQUE COMMENT '角色编码',
    role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
    description VARCHAR(255) COMMENT '描述',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

ALTER TABLE sys_user_role ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT '逻辑删除';
ALTER TABLE sys_user_role ADD COLUMN IF NOT EXISTS create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE sys_user_role ADD COLUMN IF NOT EXISTS update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    permission_code VARCHAR(128) NOT NULL UNIQUE COMMENT '权限编码',
    permission_name VARCHAR(64) NOT NULL COMMENT '权限名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    type VARCHAR(20) COMMENT '类型 menu/button/api',
    path VARCHAR(255) COMMENT '路由/API路径',
    sort_order INT DEFAULT 0 COMMENT '排序',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

ALTER TABLE sys_permission ADD COLUMN IF NOT EXISTS create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE sys_permission ADD COLUMN IF NOT EXISTS update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

ALTER TABLE sys_role_permission ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT '逻辑删除';
ALTER TABLE sys_role_permission ADD COLUMN IF NOT EXISTS create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE sys_role_permission ADD COLUMN IF NOT EXISTS update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

CREATE TABLE IF NOT EXISTS lawyer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id BIGINT COMMENT '关联用户ID',
    name VARCHAR(64) NOT NULL COMMENT '律师姓名',
    employee_no VARCHAR(64) COMMENT '工号',
    id_card VARCHAR(18) COMMENT '身份证号',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    entry_date DATE COMMENT '入职日期',
    commission_rate DECIMAL(5,4) DEFAULT 0.8500 COMMENT '提成比例',
    opening_balance DECIMAL(18,2) DEFAULT 0.00 COMMENT '期初余额',
    status TINYINT DEFAULT 1 COMMENT '状态',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='律师信息表';

CREATE TABLE IF NOT EXISTS client (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name VARCHAR(128) NOT NULL COMMENT '姓名/名称',
    client_type TINYINT DEFAULT 1 COMMENT '1委托人 2当事人',
    phone VARCHAR(20) COMMENT '联系电话',
    id_card VARCHAR(18) COMMENT '身份证号',
    address VARCHAR(255) COMMENT '地址',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='委托人/当事人表';

CREATE TABLE IF NOT EXISTS project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    project_no VARCHAR(64) NOT NULL UNIQUE COMMENT '项目编号',
    project_name VARCHAR(255) NOT NULL COMMENT '项目/案件名称',
    client_id BIGINT COMMENT '委托人ID',
    party_id BIGINT COMMENT '当事人ID',
    case_cause VARCHAR(255) COMMENT '案由',
    party_identity VARCHAR(64) COMMENT '当事人身份',
    business_type VARCHAR(64) COMMENT '业务类别',
    host_lawyer_id BIGINT COMMENT '主办律师ID',
    assist_lawyer_id BIGINT COMMENT '协办律师ID',
    case_date DATE COMMENT '收案日期',
    contract_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '合同金额',
    received_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '回款金额',
    receive_ratio DECIMAL(5,2) DEFAULT 0.00 COMMENT '回款比例',
    archive_fee DECIMAL(18,2) DEFAULT 0.00 COMMENT '档案费',
    archive_status TINYINT DEFAULT 0 COMMENT '归档状态 0未归档 1已归档',
    case_status TINYINT DEFAULT 0 COMMENT '案件状态 0进行中 1已结案',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目/案件表';

CREATE TABLE IF NOT EXISTS project_income (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    project_id BIGINT NOT NULL COMMENT '项目ID',
    income_date DATE NOT NULL COMMENT '收入日期',
    income_amount DECIMAL(18,2) NOT NULL COMMENT '收入总额',
    host_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '主办律师收入',
    assist_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '协办律师收入',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目收入表';

CREATE TABLE IF NOT EXISTS account_transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    lawyer_id BIGINT NOT NULL COMMENT '律师ID',
    txn_date DATE NOT NULL COMMENT '交易日期',
    txn_month VARCHAR(7) NOT NULL COMMENT '交易月份 yyyy-MM',
    project_id BIGINT COMMENT '关联项目ID',
    project_income_id BIGINT COMMENT '关联项目收入ID',
    txn_type VARCHAR(32) NOT NULL COMMENT '交易类型',
    amount DECIMAL(18,2) NOT NULL COMMENT '发生金额（收入为正，支出为负）',
    balance DECIMAL(18,2) NOT NULL COMMENT '交易后余额',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户交易流水表';

CREATE TABLE IF NOT EXISTS social_insurance_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    year INT NOT NULL COMMENT '年度',
    month INT NOT NULL COMMENT '月份',
    base_amount DECIMAL(18,2) NOT NULL COMMENT '缴费基数',
    pension_rate_personal DECIMAL(6,4) DEFAULT 0.0800 COMMENT '养老个人比例',
    pension_rate_company DECIMAL(6,4) DEFAULT 0.1600 COMMENT '养老单位比例',
    medical_rate_personal DECIMAL(6,4) DEFAULT 0.0200 COMMENT '医疗个人比例',
    medical_rate_company DECIMAL(6,4) DEFAULT 0.0900 COMMENT '医疗单位比例',
    unemployment_rate_personal DECIMAL(6,4) DEFAULT 0.0050 COMMENT '失业个人比例',
    unemployment_rate_company DECIMAL(6,4) DEFAULT 0.0050 COMMENT '失业单位比例',
    injury_rate_company DECIMAL(6,4) DEFAULT 0.0050 COMMENT '工伤单位比例',
    maternity_rate_company DECIMAL(6,4) DEFAULT 0.0080 COMMENT '生育单位比例',
    housing_rate_personal DECIMAL(6,4) DEFAULT 0.0500 COMMENT '公积金个人比例',
    housing_rate_company DECIMAL(6,4) DEFAULT 0.0500 COMMENT '公积金单位比例',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_si_config_year_month (year, month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社保公积金配置表';

CREATE TABLE IF NOT EXISTS social_insurance_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    lawyer_id BIGINT NOT NULL COMMENT '律师ID',
    year INT NOT NULL COMMENT '年度',
    month INT NOT NULL COMMENT '月份',
    base_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '缴费基数',
    pension_personal DECIMAL(18,2) DEFAULT 0.00 COMMENT '养老个人',
    pension_company DECIMAL(18,2) DEFAULT 0.00 COMMENT '养老单位',
    medical_personal DECIMAL(18,2) DEFAULT 0.00 COMMENT '医疗个人',
    medical_company DECIMAL(18,2) DEFAULT 0.00 COMMENT '医疗单位',
    unemployment_personal DECIMAL(18,2) DEFAULT 0.00 COMMENT '失业个人',
    unemployment_company DECIMAL(18,2) DEFAULT 0.00 COMMENT '失业单位',
    injury_company DECIMAL(18,2) DEFAULT 0.00 COMMENT '工伤单位',
    maternity_company DECIMAL(18,2) DEFAULT 0.00 COMMENT '生育单位',
    housing_personal DECIMAL(18,2) DEFAULT 0.00 COMMENT '公积金个人',
    housing_company DECIMAL(18,2) DEFAULT 0.00 COMMENT '公积金单位',
    total_personal DECIMAL(18,2) DEFAULT 0.00 COMMENT '个人合计',
    total_company DECIMAL(18,2) DEFAULT 0.00 COMMENT '单位合计',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_si_record_lawyer_year_month (lawyer_id, year, month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社保公积金记录表';

CREATE TABLE IF NOT EXISTS tax_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    lawyer_id BIGINT NOT NULL COMMENT '律师ID',
    year INT NOT NULL COMMENT '年度',
    month INT NOT NULL COMMENT '月份',
    income_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '当月收入',
    deductible_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '减除费用',
    special_deduction DECIMAL(18,2) DEFAULT 0.00 COMMENT '专项扣除',
    additional_deduction DECIMAL(18,2) DEFAULT 0.00 COMMENT '专项附加扣除',
    other_deduction DECIMAL(18,2) DEFAULT 0.00 COMMENT '其他扣除',
    taxable_income DECIMAL(18,2) DEFAULT 0.00 COMMENT '应纳税所得额',
    tax_rate DECIMAL(6,4) DEFAULT 0.0000 COMMENT '税率',
    quick_deduction DECIMAL(18,2) DEFAULT 0.00 COMMENT '速算扣除数',
    tax_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '应缴税额',
    paid_tax DECIMAL(18,2) DEFAULT 0.00 COMMENT '已纳税额',
    cumulative_income DECIMAL(18,2) DEFAULT 0.00 COMMENT '累计收入',
    cumulative_taxable DECIMAL(18,2) DEFAULT 0.00 COMMENT '累计应纳税所得额',
    cumulative_tax DECIMAL(18,2) DEFAULT 0.00 COMMENT '累计应纳税额',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_tax_record_lawyer_year_month (lawyer_id, year, month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个税记录表';

CREATE TABLE IF NOT EXISTS archive_fee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    project_id BIGINT NOT NULL COMMENT '项目ID',
    lawyer_id BIGINT NOT NULL COMMENT '律师ID',
    fee_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '档案费金额',
    archive_status TINYINT DEFAULT 0 COMMENT '归档状态',
    archive_date DATE COMMENT '归档日期',
    refund_status TINYINT DEFAULT 0 COMMENT '退还状态',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='档案费表';

CREATE TABLE IF NOT EXISTS expense (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    lawyer_id BIGINT NOT NULL COMMENT '律师ID',
    expense_date DATE NOT NULL COMMENT '支出日期',
    expense_month VARCHAR(7) NOT NULL COMMENT '支出月份',
    expense_type VARCHAR(32) NOT NULL COMMENT '支出类型',
    amount DECIMAL(18,2) NOT NULL COMMENT '金额',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支出表';

CREATE TABLE IF NOT EXISTS sys_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    config_key VARCHAR(128) NOT NULL UNIQUE COMMENT '配置键',
    config_value VARCHAR(512) COMMENT '配置值',
    description VARCHAR(255) COMMENT '描述',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

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
