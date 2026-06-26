package com.lawfirm.caseledger.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SocialInsuranceConfigDto {

    private Long id;
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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
