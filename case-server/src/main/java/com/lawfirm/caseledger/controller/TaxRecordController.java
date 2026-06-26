package com.lawfirm.caseledger.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.caseledger.common.Result;
import com.lawfirm.caseledger.dto.TaxRecordDto;
import com.lawfirm.caseledger.service.TaxRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tax-records")
@RequiredArgsConstructor
public class TaxRecordController {

    private final TaxRecordService taxRecordService;

    @GetMapping
    @PreAuthorize("hasAuthority('tax:record')")
    public Result<Page<TaxRecordDto>> page(
            @RequestParam(required = false) Long lawyerId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(taxRecordService.pageRecords(lawyerId, year, month, page, size));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('tax:record')")
    public Result<Integer> generate(
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestParam(required = false) List<Long> lawyerIds) {
        return Result.success(taxRecordService.generateRecords(year, month, lawyerIds));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('tax:record')")
    public Result<Void> delete(@PathVariable Long id) {
        taxRecordService.deleteRecord(id);
        return Result.success();
    }
}
