package com.lawfirm.caseledger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientRequest {

    @NotBlank(message = "请输入姓名/名称")
    private String name;

    /** 1 委托人 / 2 当事人 */
    @NotNull(message = "请选择类型")
    private Integer clientType;

    private String phone;

    /** 18 位身份证号；格式校验在 Bean Validation，校验位 + 解析在 service。 */
    @Pattern(regexp = "^[1-9]\\d{5}(?:18|19|20)\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$",
            message = "身份证号格式不正确")
    private String idCard;

    @Size(max = 255, message = "联系地址不超过 255 字")
    private String address;

    private String remark;
}