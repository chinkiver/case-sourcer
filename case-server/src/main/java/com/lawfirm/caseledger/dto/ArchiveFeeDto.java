package com.lawfirm.caseledger.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ArchiveFeeDto {

    private Long id;
    private Long projectId;
    private String projectNo;
    private String projectName;
    private Long lawyerId;
    private String lawyerName;
    private BigDecimal feeAmount;
    private Integer archiveStatus;
    private LocalDate archiveDate;
    private Integer refundStatus;
    private String remark;
    private LocalDateTime createTime;
}
