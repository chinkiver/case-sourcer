package com.lawfirm.caseledger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProjectRequest {

    @NotBlank(message = "项目名称不能为空")
    private String projectName;

    private String projectNo;

    @NotNull(message = "委托人不能为空")
    private Long clientId;

    private Long partyId;
    private String caseCause;
    private String partyIdentity;

    @NotBlank(message = "业务类别不能为空")
    private String businessType;

    @NotNull(message = "主办律师不能为空")
    private Long hostLawyerId;

    private Long assistLawyerId;

    @NotNull(message = "收案日期不能为空")
    private LocalDate caseDate;

    @NotNull(message = "合同金额不能为空")
    private BigDecimal contractAmount;

    private BigDecimal archiveFee;
    private String remark;
}
