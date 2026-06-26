package com.lawfirm.caseledger.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("social_insurance_record")
public class SocialInsuranceRecord extends BaseEntity {

    private Long lawyerId;
    private Integer year;
    private Integer month;
    private BigDecimal baseAmount;
    private BigDecimal pensionPersonal;
    private BigDecimal pensionCompany;
    private BigDecimal medicalPersonal;
    private BigDecimal medicalCompany;
    private BigDecimal unemploymentPersonal;
    private BigDecimal unemploymentCompany;
    private BigDecimal injuryCompany;
    private BigDecimal maternityCompany;
    private BigDecimal housingPersonal;
    private BigDecimal housingCompany;
    private BigDecimal totalPersonal;
    private BigDecimal totalCompany;
}
