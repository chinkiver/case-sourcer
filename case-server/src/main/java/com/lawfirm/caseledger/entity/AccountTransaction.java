package com.lawfirm.caseledger.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("account_transaction")
public class AccountTransaction extends BaseEntity {

    private Long lawyerId;
    private LocalDate txnDate;
    private String txnMonth;
    private Long projectId;
    private Long projectIncomeId;
    private String txnType;
    private BigDecimal amount;
    private BigDecimal balance;
    private String remark;
}
