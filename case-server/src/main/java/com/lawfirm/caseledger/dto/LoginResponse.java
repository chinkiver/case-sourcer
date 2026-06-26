package com.lawfirm.caseledger.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;
    private String tokenType;
    private Long expiresIn;
    private UserInfoResponse userInfo;
}
