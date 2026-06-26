package com.lawfirm.caseledger.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProjectDto {

    private Long id;
    private String projectNo;
    private String projectName;
    private Long clientId;
    private String clientName;
    private Long partyId;
    private String partyName;
    private String caseCause;
    private String partyIdentity;
    private String businessType;
    private Long hostLawyerId;
    private String hostLawyerName;
    private Long assistLawyerId;
    private String assistLawyerName;
    private LocalDate caseDate;
    private BigDecimal contractAmount;
    private BigDecimal receivedAmount;
    private BigDecimal receiveRatio;
    private BigDecimal archiveFee;
    private Integer archiveStatus;
    private Integer caseStatus;
    private String remark;
}
