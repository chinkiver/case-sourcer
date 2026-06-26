package com.lawfirm.caseledger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseRequest {

    @NotNull(message = "律师不能为空")
    private Long lawyerId;

    @NotNull(message = "支出日期不能为空")
    private LocalDate expenseDate;

    @NotBlank(message = "支出类型不能为空")
    private String expenseType;

    @NotNull(message = "支出金额不能为空")
    private BigDecimal amount;

    private String remark;
}
