package com.lawfirm.caseledger.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.caseledger.common.BusinessException;
import com.lawfirm.caseledger.dto.UserDto;
import com.lawfirm.caseledger.dto.UserRequest;
import com.lawfirm.caseledger.entity.SysRole;
import com.lawfirm.caseledger.entity.SysUser;
import com.lawfirm.caseledger.entity.SysUserRole;
import com.lawfirm.caseledger.mapper.SysRoleMapper;
import com.lawfirm.caseledger.mapper.SysUserMapper;
import com.lawfirm.caseledger.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    public Page<UserDto> pageUsers(String keyword, Integer page, Integer size) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like("username", keyword).or().like("real_name", keyword));
        }
        wrapper.orderByDesc("create_time");
        Page<SysUser> userPage = userMapper.selectPage(new Page<>(page, size), wrapper);
        List<UserDto> records = userPage.getRecords().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        Page<UserDto> result = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        result.setRecords(records);
        return result;
    }

    public UserDto getUser(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }
        return convertToDto(user);
    }

    @Transactional
    public UserDto createUser(UserRequest request) {
        if (existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        SysUser user = new SysUser();
        BeanUtils.copyProperties(request, user);
        String password = request.getPassword() != null && !request.getPassword().isBlank()
                ? request.getPassword() : "123456";
        user.setPassword(passwordEncoder.encode(password));
        userMapper.insert(user);
        assignRoles(user.getId(), request.getRoleIds());
        return convertToDto(user);
    }

    @Transactional
    public UserDto updateUser(Long id, UserRequest request) {
        SysUser user = userMapper.selectById(id);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }
        if (!user.getUsername().equals(request.getUsername()) && existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        BeanUtils.copyProperties(request, user);
        userMapper.updateById(user);
        assignRoles(user.getId(), request.getRoleIds());
        return convertToDto(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }
        userMapper.deleteById(id);
        userRoleMapper.physicalDeleteByUserId(id);
    }

    @Transactional
    public void resetPassword(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode("123456"));
        userMapper.updateById(user);
    }

    private boolean existsByUsername(String username) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username).eq("deleted", 0);
        return userMapper.selectCount(wrapper) > 0;
    }

    private void assignRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.physicalDeleteByUserId(userId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }
        List<SysUserRole> list = new ArrayList<>();
        for (Long roleId : roleIds) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            list.add(ur);
        }
        for (SysUserRole ur : list) {
            userRoleMapper.insert(ur);
        }
    }

    private UserDto convertToDto(SysUser user) {
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(user, dto);
        List<Long> roleIds = userRoleMapper.selectList(
                        new QueryWrapper<SysUserRole>().eq("user_id", user.getId()))
                .stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        dto.setRoleIds(roleIds);
        if (!roleIds.isEmpty()) {
            List<SysRole> roles = roleMapper.selectBatchIds(roleIds);
            dto.setRoleNames(roles.stream().map(SysRole::getRoleName).collect(Collectors.toList()));
        } else {
            dto.setRoleNames(new ArrayList<>());
        }
        return dto;
    }
}
