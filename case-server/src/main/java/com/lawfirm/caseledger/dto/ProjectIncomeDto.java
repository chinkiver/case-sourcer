package com.lawfirm.caseledger.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProjectIncomeDto {

    private Long id;
    private Long projectId;
    private String projectName;
    private LocalDate incomeDate;
    private BigDecimal incomeAmount;
    private BigDecimal hostAmount;
    private BigDecimal assistAmount;
    private String remark;
    private LocalDateTime createTime;
}
