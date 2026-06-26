package com.lawfirm.caseledger.controller;

import com.lawfirm.caseledger.common.Result;
import com.lawfirm.caseledger.dto.ProjectIncomeDto;
import com.lawfirm.caseledger.dto.ProjectIncomeRequest;
import com.lawfirm.caseledger.service.IncomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/incomes")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    @PreAuthorize("hasAuthority('income:add')")
    public Result<ProjectIncomeDto> create(@Valid @RequestBody ProjectIncomeRequest request) {
        return Result.success(incomeService.createIncome(request));
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAuthority('income:add') or hasAuthority('project:list')")
    public Result<List<ProjectIncomeDto>> listByProject(@PathVariable Long projectId) {
        return Result.success(incomeService.listByProject(projectId));
    }
}
