package com.lawfirm.caseledger.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ExpenseDto {

    private Long id;
    private Long lawyerId;
    private String lawyerName;
    private LocalDate expenseDate;
    private String expenseMonth;
    private String expenseType;
    private BigDecimal amount;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
