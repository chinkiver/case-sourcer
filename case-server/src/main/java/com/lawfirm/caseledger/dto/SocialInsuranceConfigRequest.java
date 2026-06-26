package com.lawfirm.caseledger.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SocialInsuranceConfigRequest {

    @NotNull(message = "年度不能为空")
    private Integer year;

    @NotNull(message = "月份不能为空")
    private Integer month;

    @NotNull(message = "缴费基数不能为空")
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
