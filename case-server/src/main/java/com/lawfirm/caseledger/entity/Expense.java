package com.lawfirm.caseledger.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("expense")
public class Expense extends BaseEntity {

    private Long lawyerId;
    private LocalDate expenseDate;
    private String expenseMonth;
    private String expenseType;
    private BigDecimal amount;
    private String remark;
}
