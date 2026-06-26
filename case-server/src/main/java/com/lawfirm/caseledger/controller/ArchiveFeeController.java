package com.lawfirm.caseledger.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.caseledger.common.Result;
import com.lawfirm.caseledger.dto.ArchiveFeeDto;
import com.lawfirm.caseledger.dto.ArchiveFeeRequest;
import com.lawfirm.caseledger.service.ArchiveFeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/archive-fees")
@RequiredArgsConstructor
public class ArchiveFeeController {

    private final ArchiveFeeService archiveFeeService;

    @PostMapping("/archive")
    @PreAuthorize("hasAuthority('archive')")
    public Result<ArchiveFeeDto> archive(@Valid @RequestBody ArchiveFeeRequest request) {
        return Result.success(archiveFeeService.archiveProject(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('archive') or hasAuthority('ledger:view')")
    public Result<Page<ArchiveFeeDto>> page(
            @RequestParam(required = false) Long lawyerId,
            @RequestParam(required = false) Integer archiveStatus,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(archiveFeeService.pageArchiveFees(lawyerId, archiveStatus, page, size));
    }
}
