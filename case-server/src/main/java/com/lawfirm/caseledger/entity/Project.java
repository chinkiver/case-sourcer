package com.lawfirm.caseledger.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("project")
public class Project extends BaseEntity {

    private String projectNo;
    private String projectName;
    private Long clientId;
    private Long partyId;
    private String caseCause;
    private String partyIdentity;
    private String businessType;
    private Long hostLawyerId;
    private Long assistLawyerId;
    private LocalDate caseDate;
    private BigDecimal contractAmount;
    private BigDecimal receivedAmount;
    private BigDecimal receiveRatio;
    private BigDecimal archiveFee;
    private Integer archiveStatus;
    private Integer caseStatus;
    private String remark;
}
