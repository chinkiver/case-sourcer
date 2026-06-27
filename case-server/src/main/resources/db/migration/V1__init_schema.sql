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
