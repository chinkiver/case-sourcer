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

    // 分类汇总
    private BigDecimal totalProjectIncome;
    private BigDecimal totalCommission;
    private BigDecimal totalSocialInsurance;
    private BigDecimal totalTax;
    private BigDecimal totalPayout;
    private BigDecimal availableWithdrawal;
}
