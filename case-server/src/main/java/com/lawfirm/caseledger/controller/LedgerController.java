package com.lawfirm.caseledger.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.caseledger.common.Result;
import com.lawfirm.caseledger.dto.AccountSummaryDto;
import com.lawfirm.caseledger.dto.AccountTransactionDto;
import com.lawfirm.caseledger.security.SecurityUser;
import com.lawfirm.caseledger.service.AuthService;
import com.lawfirm.caseledger.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService ledgerService;
    private final AuthService authService;

    @GetMapping("/transactions")
    @PreAuthorize("hasAuthority('ledger:view')")
    public Result<Page<AccountTransactionDto>> transactions(
            @RequestParam(required = false) Long lawyerId,
            @RequestParam(required = false) String txnMonth,
            @RequestParam(required = false) String txnType,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        SecurityUser currentUser = authService.getCurrentUser();
        return Result.success(ledgerService.pageTransactions(lawyerId, txnMonth, txnType, page, size, currentUser));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('ledger:view')")
    public Result<AccountSummaryDto> summary(@RequestParam(required = false) Long lawyerId) {
        SecurityUser currentUser = authService.getCurrentUser();
        return Result.success(ledgerService.getSummary(lawyerId, currentUser));
    }
}
