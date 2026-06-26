package com.lawfirm.caseledger.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoleDto {

    private Long id;
    private String roleCode;
    private String roleName;
    private String description;
    private List<Long> permissionIds;
    private List<String> permissionNames;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
