package com.lawfirm.caseledger.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ArchiveFeeRequest {

    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @NotNull(message = "律师ID不能为空")
    private Long lawyerId;

    private LocalDate archiveDate;

    private String remark;
}
