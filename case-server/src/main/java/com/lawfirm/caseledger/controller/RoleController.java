package com.lawfirm.caseledger.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.caseledger.common.Result;
import com.lawfirm.caseledger.dto.RoleDto;
import com.lawfirm.caseledger.dto.RoleRequest;
import com.lawfirm.caseledger.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAuthority('system:role')")
    public Result<Page<RoleDto>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(roleService.pageRoles(keyword, page, size));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('system:role') or hasAuthority('system:user')")
    public Result<List<RoleDto>> listAll() {
        return Result.success(roleService.listAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role')")
    public Result<RoleDto> get(@PathVariable Long id) {
        return Result.success(roleService.getRole(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:role')")
    public Result<RoleDto> create(@Valid @RequestBody RoleRequest request) {
        return Result.success(roleService.createRole(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role')")
    public Result<RoleDto> update(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        return Result.success(roleService.updateRole(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role')")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }
}
