package com.lawfirm.caseledger.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lawyer")
public class Lawyer extends BaseEntity {

    private Long userId;
    private String name;
    private String employeeNo;
    private String idCard;
    private String phone;
    private String email;
    private LocalDate entryDate;
    private BigDecimal commissionRate;
    private BigDecimal openingBalance;
    private Integer status;
}
