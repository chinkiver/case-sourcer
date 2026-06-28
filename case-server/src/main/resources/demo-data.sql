-- 演示数据
-- 说明：本文件为可选数据，用于系统功能体验。新建数据库时先执行 init.sql，再按需执行本文件。

USE case_ledger;

-- 新增律师
INSERT IGNORE INTO lawyer (id, name, employee_no, phone, id_card, entry_date, commission_rate, opening_balance, status) VALUES
(4, '王芳', 'L004', '13800000004', '110101199001010004', '2023-03-01', 0.85, 0.00, 1),
(5, '赵子涵', 'L005', '13800000005', '110101199201010005', '2022-07-15', 0.85, 0.00, 1);

-- 新增委托人 / 当事人
INSERT IGNORE INTO client (id, name, client_type, phone, id_card, address) VALUES
(4, '北京某科技有限公司', 1, '010-12345678', '91110108MA00XXXX1X', '北京市海淀区'),
(5, '陈六', 1, '13900000004', '110101198801010006', '北京市朝阳区'),
(6, '刘七', 2, '13900000005', '110101199501010007', '北京市东城区'),
(7, '某建设工程有限公司', 1, '010-87654321', '91110108MA00XXXX2Y', '北京市丰台区');

-- 项目信息
INSERT IGNORE INTO project (id, project_no, project_name, client_id, party_id, case_cause, party_identity, business_type, host_lawyer_id, assist_lawyer_id, case_date, contract_amount, archive_fee, received_amount, receive_ratio, archive_status, case_status, remark) VALUES
(101, 'MS202501001', '张三诉李四合同纠纷案', 1, 2, '合同纠纷', '原告', 'MS', 1, 2, '2025-01-10', 100000.00, 1000.00, 50000.00, 0.5000, 1, 0, '一审阶段，已归档'),
(102, 'XS202502001', '王五故意伤害案', 3, 3, '故意伤害', '被告人', 'XS', 1, NULL, '2025-02-05', 60000.00, 1000.00, 60000.00, 1.0000, 0, 1, '已结案'),
(103, 'GW202502001', '某科技公司常年法律顾问', 4, 4, '法律顾问', '顾问单位', 'GW', 2, NULL, '2025-02-01', 120000.00, 2000.00, 40000.00, 0.3333, 0, 0, '年度顾问'),
(104, 'LD202502001', '刘七劳动争议仲裁案', 5, 6, '劳动争议', '申请人', 'LD', 3, 1, '2025-02-15', 50000.00, 500.00, 50000.00, 1.0000, 0, 1, '已结案'),
(105, 'XZ202502001', '某建设公司行政处罚复议案', 7, 7, '行政复议', '申请人', 'XZ', 2, 3, '2025-02-20', 80000.00, 1000.00, 80000.00, 1.0000, 0, 1, '已结案');

-- 项目收入登记
INSERT IGNORE INTO project_income (id, project_id, income_date, income_amount, host_amount, assist_amount, remark) VALUES
(1001, 101, '2025-01-15', 30000.00, 25500.00, 0.00, '第一阶段律师费'),
(1002, 101, '2025-02-10', 20000.00, 17000.00, 0.00, '第二阶段律师费'),
(1003, 102, '2025-02-05', 10000.00, 8500.00, 0.00, '刑事辩护费'),
(1004, 103, '2025-02-18', 10000.00, 8500.00, 0.00, '顾问费首期'),
(1005, 104, '2025-02-20', 20000.00, 17000.00, 0.00, '劳动仲裁代理费'),
(1006, 105, '2025-02-25', 20000.00, 17000.00, 0.00, '行政复议代理费');

-- 律师账户流水（按时间顺序，模拟真实资金变动）
INSERT IGNORE INTO account_transaction (id, lawyer_id, txn_date, txn_month, txn_type, amount, balance, remark) VALUES
-- 闫国强
(10001, 1, '2025-01-15', '2025-01', '账户增加', 25500.00, 25500.00, '张三诉李四合同纠纷案 提成'),
(10002, 1, '2025-02-05', '2025-02', '账户增加', 8500.00, 34000.00, '王五故意伤害案 提成'),
(10003, 1, '2025-02-10', '2025-02', '账户增加', 17000.00, 51000.00, '张三诉李四合同纠纷案 提成'),
(10004, 1, '2025-02-12', '2025-02', '暂扣档案费', -1000.00, 50000.00, '项目 MS202501001 档案费暂扣'),
(10005, 1, '2025-02-28', '2025-02', '社保公积金扣款', -2956.27, 47043.73, '2025-02 社保公积金个人部分'),
(10006, 1, '2025-02-28', '2025-02', '个税扣缴', -513.01, 46530.72, '2025-02 个税扣缴'),
(10007, 1, '2025-02-28', '2025-02', '工资发放', -30000.00, 16530.72, '2025-02 工资发放'),
-- 张耀华
(10008, 2, '2025-02-18', '2025-02', '账户增加', 8500.00, 8500.00, '某科技公司常年法律顾问 提成'),
(10009, 2, '2025-02-25', '2025-02', '账户增加', 17000.00, 25500.00, '某建设公司行政复议 提成'),
(10010, 2, '2025-02-28', '2025-02', '社保公积金扣款', -2956.27, 22543.73, '2025-02 社保公积金个人部分'),
(10011, 2, '2025-02-28', '2025-02', '工资发放', -2000.00, 20543.73, '2025-02 工资发放'),
-- 白一冰
(10012, 3, '2025-02-20', '2025-02', '账户增加', 17000.00, 17000.00, '刘七劳动争议仲裁案 提成'),
(10013, 3, '2025-02-28', '2025-02', '社保公积金扣款', -2956.27, 14043.73, '2025-02 社保公积金个人部分');

-- 支出登记
INSERT IGNORE INTO expense (id, lawyer_id, expense_date, expense_month, expense_type, amount, remark) VALUES
(1, 1, '2025-02-28', '2025-02', '工资发放', 30000.00, '2025-02 工资发放'),
(2, 2, '2025-02-28', '2025-02', '工资发放', 2000.00, '2025-02 工资发放'),
(3, 1, '2025-02-26', '2025-02', '个人报销', 1200.00, '出差交通费');

-- 档案费归档记录
INSERT IGNORE INTO archive_fee (id, project_id, lawyer_id, fee_amount, archive_status, archive_date, refund_status, remark) VALUES
(1, 101, 1, 1000.00, 1, '2025-02-12', 1, '项目已归档，档案费已退还');

-- 社保公积金配置（2025年2月）
INSERT IGNORE INTO social_insurance_config (id, year, month, base_amount, pension_rate_personal, pension_rate_company, medical_rate_personal, medical_rate_company, unemployment_rate_personal, unemployment_rate_company, injury_rate_company, maternity_rate_company, housing_rate_personal, housing_rate_company) VALUES
(1, 2025, 2, 6326.00, 0.0800, 0.1600, 0.0200, 0.0900, 0.0050, 0.0050, 0.0050, 0.0080, 0.0500, 0.0500);

-- 社保公积金扣缴记录（2025年2月）
INSERT IGNORE INTO social_insurance_record (id, lawyer_id, year, month, base_amount, pension_personal, pension_company, medical_personal, medical_company, unemployment_personal, unemployment_company, injury_company, maternity_company, housing_personal, housing_company, total_personal, total_company) VALUES
(1, 1, 2025, 2, 6326.00, 506.08, 1012.16, 126.52, 569.34, 31.63, 31.63, 31.63, 50.61, 316.30, 316.30, 2956.27, 1989.04),
(2, 2, 2025, 2, 6326.00, 506.08, 1012.16, 126.52, 569.34, 31.63, 31.63, 31.63, 50.61, 316.30, 316.30, 2956.27, 1989.04),
(3, 3, 2025, 2, 6326.00, 506.08, 1012.16, 126.52, 569.34, 31.63, 31.63, 31.63, 50.61, 316.30, 316.30, 2956.27, 1989.04);

-- 个税扣缴记录（2025年2月）
INSERT IGNORE INTO tax_record (id, lawyer_id, year, month, income_amount, deductible_amount, special_deduction, additional_deduction, other_deduction, taxable_income, tax_rate, quick_deduction, tax_amount, paid_tax, cumulative_income, cumulative_taxable, cumulative_tax) VALUES
(1, 1, 2025, 2, 25500.00, 5000.00, 2956.27, 0.00, 0.00, 14543.73, 0.03, 0.00, 513.01, 0.00, 42500.00, 14543.73, 513.01);
