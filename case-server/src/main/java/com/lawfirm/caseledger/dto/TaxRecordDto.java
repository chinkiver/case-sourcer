package com.lawfirm.caseledger.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TaxRecordDto {

    private Long id;
    private Long lawyerId;
    private String lawyerName;
    private Integer year;
    private Integer month;
    private BigDecimal incomeAmount;
    private BigDecimal deductibleAmount;
    private BigDecimal specialDeduction;
    private BigDecimal additionalDeduction;
    private BigDecimal otherDeduction;
    private BigDecimal taxableIncome;
    private BigDecimal taxRate;
    private BigDecimal quickDeduction;
    private BigDecimal taxAmount;
    private BigDecimal paidTax;
    private BigDecimal cumulativeIncome;
    private BigDecimal cumulativeTaxable;
    private BigDecimal cumulativeTax;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
