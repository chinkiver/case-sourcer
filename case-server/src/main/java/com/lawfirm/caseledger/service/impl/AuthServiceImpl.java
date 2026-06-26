package com.lawfirm.caseledger.service.impl;

import com.lawfirm.caseledger.common.BusinessException;
import com.lawfirm.caseledger.dto.LoginRequest;
import com.lawfirm.caseledger.dto.LoginResponse;
import com.lawfirm.caseledger.dto.PasswordChangeRequest;
import com.lawfirm.caseledger.dto.UserInfoResponse;
import com.lawfirm.caseledger.entity.SysUser;
import com.lawfirm.caseledger.mapper.SysUserMapper;
import com.lawfirm.caseledger.security.JwtUtil;
import com.lawfirm.caseledger.security.SecurityUser;
import com.lawfirm.caseledger.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            String token = jwtUtil.generateToken(securityUser.getUserId(), securityUser.getUsername());

            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setTokenType("Bearer");
            response.setExpiresIn(86400L);
            response.setUserInfo(buildUserInfo(securityUser));
            return response;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("用户名或密码错误");
        }
    }

    @Override
    public UserInfoResponse getCurrentUserInfo() {
        SecurityUser user = getCurrentUser();
        return buildUserInfo(user);
    }

    @Override
    public void changePassword(PasswordChangeRequest request) {
        SecurityUser securityUser = getCurrentUser();
        SysUser user = sysUserMapper.selectById(securityUser.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        sysUserMapper.updateById(user);
    }

    @Override
    public SecurityUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser)) {
            throw new BusinessException(401, "用户未登录");
        }
        return (SecurityUser) authentication.getPrincipal();
    }

    private UserInfoResponse buildUserInfo(SecurityUser securityUser) {
        UserInfoResponse info = new UserInfoResponse();
        BeanUtils.copyProperties(securityUser, info);
        info.setUserId(securityUser.getUserId());
        List<String> roles = sysUserMapper.selectRoleCodesByUserId(securityUser.getUserId());
        info.setRoles(roles);
        List<String> permissions = sysUserMapper.selectPermissionCodesByUserId(securityUser.getUserId());
        info.setPermissions(permissions);
        return info;
    }
}
