package com.lawfirm.caseledger.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AccountTransactionDto {

    private Long id;
    private Long lawyerId;
    private String lawyerName;
    private LocalDate txnDate;
    private String txnMonth;
    private Long projectId;
    private String projectNo;
    private String projectName;
    private Long projectIncomeId;
    private String txnType;
    private BigDecimal amount;
    private BigDecimal balance;
    private String remark;
    private LocalDateTime createTime;
}
