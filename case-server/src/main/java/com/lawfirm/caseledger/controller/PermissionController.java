package com.lawfirm.caseledger.controller;

import com.lawfirm.caseledger.common.Result;
import com.lawfirm.caseledger.entity.SysPermission;
import com.lawfirm.caseledger.mapper.SysPermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final SysPermissionMapper permissionMapper;

    @GetMapping
    @PreAuthorize("hasAuthority('system:role')")
    public Result<List<PermissionNode>> list() {
        List<SysPermission> all = permissionMapper.selectList(null);
        List<PermissionNode> tree = buildTree(all, 0L);
        return Result.success(tree);
    }

    private List<PermissionNode> buildTree(List<SysPermission> all, Long parentId) {
        return all.stream()
                .filter(p -> p.getParentId() != null && p.getParentId().equals(parentId))
                .map(p -> {
                    PermissionNode node = new PermissionNode();
                    node.setId(p.getId());
                    node.setPermissionCode(p.getPermissionCode());
                    node.setPermissionName(p.getPermissionName());
                    node.setType(p.getType());
                    node.setChildren(buildTree(all, p.getId()));
                    return node;
                })
                .collect(Collectors.toList());
    }

    public static class PermissionNode {
        private Long id;
        private String permissionCode;
        private String permissionName;
        private String type;
        private List<PermissionNode> children = new ArrayList<>();

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getPermissionCode() { return permissionCode; }
        public void setPermissionCode(String permissionCode) { this.permissionCode = permissionCode; }
        public String getPermissionName() { return permissionName; }
        public void setPermissionName(String permissionName) { this.permissionName = permissionName; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public List<PermissionNode> getChildren() { return children; }
        public void setChildren(List<PermissionNode> children) { this.children = children; }
    }
}
