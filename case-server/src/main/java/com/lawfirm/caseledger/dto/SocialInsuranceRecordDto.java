package com.lawfirm.caseledger.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SocialInsuranceRecordDto {

    private Long id;
    private Long lawyerId;
    private String lawyerName;
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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
