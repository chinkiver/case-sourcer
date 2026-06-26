package com.lawfirm.caseledger.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("project_income")
public class ProjectIncome extends BaseEntity {

    private Long projectId;
    private LocalDate incomeDate;
    private BigDecimal incomeAmount;
    private BigDecimal hostAmount;
    private BigDecimal assistAmount;
    private String remark;
}
