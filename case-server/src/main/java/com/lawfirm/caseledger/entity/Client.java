package com.lawfirm.caseledger.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
}
