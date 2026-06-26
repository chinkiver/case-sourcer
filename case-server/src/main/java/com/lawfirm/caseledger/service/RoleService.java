package com.lawfirm.caseledger.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.caseledger.common.BusinessException;
import com.lawfirm.caseledger.dto.RoleDto;
import com.lawfirm.caseledger.dto.RoleRequest;
import com.lawfirm.caseledger.entity.SysPermission;
import com.lawfirm.caseledger.entity.SysRole;
import com.lawfirm.caseledger.entity.SysRolePermission;
import com.lawfirm.caseledger.mapper.SysPermissionMapper;
import com.lawfirm.caseledger.mapper.SysRoleMapper;
import com.lawfirm.caseledger.mapper.SysRolePermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final SysRoleMapper roleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;
    private final SysPermissionMapper permissionMapper;

    public Page<RoleDto> pageRoles(String keyword, Integer page, Integer size) {
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like("role_code", keyword).or().like("role_name", keyword));
        }
        wrapper.orderByDesc("create_time");
        Page<SysRole> rolePage = roleMapper.selectPage(new Page<>(page, size), wrapper);
        List<RoleDto> records = rolePage.getRecords().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        Page<RoleDto> result = new Page<>(rolePage.getCurrent(), rolePage.getSize(), rolePage.getTotal());
        result.setRecords(records);
        return result;
    }

    public List<RoleDto> listAll() {
        List<SysRole> roles = roleMapper.selectList(new QueryWrapper<SysRole>().eq("deleted", 0));
        return roles.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public RoleDto getRole(Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null || role.getDeleted() == 1) {
            throw new BusinessException("角色不存在");
        }
        return convertToDto(role);
    }

    @Transactional
    public RoleDto createRole(RoleRequest request) {
        if (existsByCode(request.getRoleCode())) {
            throw new BusinessException("角色编码已存在");
        }
        SysRole role = new SysRole();
        BeanUtils.copyProperties(request, role);
        roleMapper.insert(role);
        assignPermissions(role.getId(), request.getPermissionIds());
        return convertToDto(role);
    }

    @Transactional
    public RoleDto updateRole(Long id, RoleRequest request) {
        SysRole role = roleMapper.selectById(id);
        if (role == null || role.getDeleted() == 1) {
            throw new BusinessException("角色不存在");
        }
        if (!role.getRoleCode().equals(request.getRoleCode()) && existsByCode(request.getRoleCode())) {
            throw new BusinessException("角色编码已存在");
        }
        BeanUtils.copyProperties(request, role);
        roleMapper.updateById(role);
        assignPermissions(role.getId(), request.getPermissionIds());
        return convertToDto(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null || role.getDeleted() == 1) {
            throw new BusinessException("角色不存在");
        }
        roleMapper.deleteById(id);
        rolePermissionMapper.delete(new QueryWrapper<SysRolePermission>().eq("role_id", id));
    }

    private boolean existsByCode(String roleCode) {
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.eq("role_code", roleCode).eq("deleted", 0);
        return roleMapper.selectCount(wrapper) > 0;
    }

    private void assignPermissions(Long roleId, List<Long> permissionIds) {
        rolePermissionMapper.delete(new QueryWrapper<SysRolePermission>().eq("role_id", roleId));
        if (CollectionUtils.isEmpty(permissionIds)) {
            return;
        }
        List<SysRolePermission> list = new ArrayList<>();
        for (Long permissionId : permissionIds) {
            SysRolePermission rp = new SysRolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permissionId);
            list.add(rp);
        }
        for (SysRolePermission rp : list) {
            rolePermissionMapper.insert(rp);
        }
    }

    private RoleDto convertToDto(SysRole role) {
        RoleDto dto = new RoleDto();
        BeanUtils.copyProperties(role, dto);
        List<Long> permissionIds = rolePermissionMapper.selectList(
                        new QueryWrapper<SysRolePermission>().eq("role_id", role.getId()))
                .stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList());
        dto.setPermissionIds(permissionIds);
        if (!permissionIds.isEmpty()) {
            List<SysPermission> permissions = permissionMapper.selectBatchIds(permissionIds);
            dto.setPermissionNames(permissions.stream().map(SysPermission::getPermissionName).collect(Collectors.toList()));
        } else {
            dto.setPermissionNames(new ArrayList<>());
        }
        return dto;
    }
}
