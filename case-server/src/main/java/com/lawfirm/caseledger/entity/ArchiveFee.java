package com.lawfirm.caseledger.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("archive_fee")
public class ArchiveFee extends BaseEntity {

    private Long projectId;
    private Long lawyerId;
    private BigDecimal feeAmount;
    private Integer archiveStatus;
    private LocalDate archiveDate;
    private Integer refundStatus;
    private String remark;
}
