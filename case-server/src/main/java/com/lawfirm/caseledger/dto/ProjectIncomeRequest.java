package com.lawfirm.caseledger.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProjectIncomeRequest {

    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @NotNull(message = "收入日期不能为空")
    private LocalDate incomeDate;

    @NotNull(message = "收入总额不能为空")
    private BigDecimal incomeAmount;

    private BigDecimal hostAmount;

    private BigDecimal assistAmount;

    private String remark;
}
