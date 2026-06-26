package com.lawfirm.caseledger.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoResponse {

    private Long userId;
    private String username;
    private String realName;
    private String avatar;
    private List<String> roles;
    private List<String> permissions;
}
