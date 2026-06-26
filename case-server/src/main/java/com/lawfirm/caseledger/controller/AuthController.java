package com.lawfirm.caseledger.controller;

import com.lawfirm.caseledger.common.Result;
import com.lawfirm.caseledger.dto.LoginRequest;
import com.lawfirm.caseledger.dto.LoginResponse;
import com.lawfirm.caseledger.dto.PasswordChangeRequest;
import com.lawfirm.caseledger.dto.UserInfoResponse;
import com.lawfirm.caseledger.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @GetMapping("/info")
    public Result<UserInfoResponse> info() {
        return Result.success(authService.getCurrentUserInfo());
    }

    @PostMapping("/change-password")
    public Result<Void> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        authService.changePassword(request);
        return Result.success();
    }
}
