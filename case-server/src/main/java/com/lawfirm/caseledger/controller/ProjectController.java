package com.lawfirm.caseledger.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.caseledger.common.Result;
import com.lawfirm.caseledger.dto.ProjectDto;
import com.lawfirm.caseledger.dto.ProjectRequest;
import com.lawfirm.caseledger.service.ProjectService;
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

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasAuthority('project:add')")
    public Result<ProjectDto> create(@Valid @RequestBody ProjectRequest request) {
        return Result.success(projectService.createProject(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('project:edit')")
    public Result<ProjectDto> update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return Result.success(projectService.updateProject(id, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('project:list')")
    public Result<ProjectDto> get(@PathVariable Long id) {
        return Result.success(projectService.getProject(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('project:list')")
    public Result<Page<ProjectDto>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long lawyerId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(projectService.pageProjects(keyword, lawyerId, page, size));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('project:edit')")
    public Result<Void> delete(@PathVariable Long id) {
        projectService.deleteProject(id);
        return Result.success();
    }
}
