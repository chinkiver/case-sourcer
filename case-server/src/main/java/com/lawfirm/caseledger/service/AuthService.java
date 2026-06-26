package com.lawfirm.caseledger.service;

import com.lawfirm.caseledger.dto.LoginRequest;
import com.lawfirm.caseledger.dto.LoginResponse;
import com.lawfirm.caseledger.dto.PasswordChangeRequest;
import com.lawfirm.caseledger.dto.UserInfoResponse;
import com.lawfirm.caseledger.security.SecurityUser;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    UserInfoResponse getCurrentUserInfo();

    void changePassword(PasswordChangeRequest request);

    SecurityUser getCurrentUser();
}
