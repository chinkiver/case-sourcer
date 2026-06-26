package com.lawfirm.caseledger.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("social_insurance_config")
public class SocialInsuranceConfig extends BaseEntity {

    private Integer year;
    private Integer month;
    private BigDecimal baseAmount;
    private BigDecimal pensionRatePersonal;
    private BigDecimal pensionRateCompany;
    private BigDecimal medicalRatePersonal;
    private BigDecimal medicalRateCompany;
    private BigDecimal unemploymentRatePersonal;
    private BigDecimal unemploymentRateCompany;
    private BigDecimal injuryRateCompany;
    private BigDecimal maternityRateCompany;
    private BigDecimal housingRatePersonal;
    private BigDecimal housingRateCompany;
}
