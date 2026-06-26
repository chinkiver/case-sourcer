package com.lawfirm.caseledger.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tax_record")
public class TaxRecord extends BaseEntity {

    private Long lawyerId;
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
}
