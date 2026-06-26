package com.lawfirm.caseledger.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.caseledger.common.Result;
import com.lawfirm.caseledger.dto.ExpenseDto;
import com.lawfirm.caseledger.dto.ExpenseRequest;
import com.lawfirm.caseledger.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    @PreAuthorize("hasAuthority('expense')")
    public Result<Page<ExpenseDto>> page(
            @RequestParam(required = false) Long lawyerId,
            @RequestParam(required = false) String expenseType,
            @RequestParam(required = false) String expenseMonth,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(expenseService.pageExpenses(lawyerId, expenseType, expenseMonth, page, size));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('expense:add')")
    public Result<ExpenseDto> create(@Valid @RequestBody ExpenseRequest request) {
        return Result.success(expenseService.createExpense(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('expense:add')")
    public Result<Void> delete(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return Result.success();
    }
}
