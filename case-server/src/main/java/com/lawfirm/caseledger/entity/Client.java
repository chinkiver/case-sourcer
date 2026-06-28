package com.lawfirm.caseledger.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("client")
public class Client extends BaseEntity {

    private String name;
    private Integer clientType;
    private String phone;
    private String idCard;
    private String address;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    /** 性别，从身份证号解析得到；存 "男" / "女"。 */
    private String gender;

    /** 仅展示用，不持久化：基于 birthDate 与当前日期计算。 */
    @TableField(exist = false)
    private Integer age;
}