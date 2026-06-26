package com.lawfirm.caseledger.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountSummaryDto {

    private Long lawyerId;
    private String lawyerName;
    private BigDecimal openingBalance;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal currentBalance;
    private BigDecimal pendingArchiveFee;
}
