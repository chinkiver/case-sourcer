package com.lawfirm.caseledger.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.caseledger.common.Result;
import com.lawfirm.caseledger.dto.SocialInsuranceRecordDto;
import com.lawfirm.caseledger.service.SocialInsuranceRecordService;
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
@RequestMapping("/api/social-insurance-records")
@RequiredArgsConstructor
public class SocialInsuranceRecordController {

    private final SocialInsuranceRecordService recordService;

    @GetMapping
    @PreAuthorize("hasAuthority('social:record') or hasAuthority('social:config')")
    public Result<Page<SocialInsuranceRecordDto>> page(
            @RequestParam(required = false) Long lawyerId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(recordService.pageRecords(lawyerId, year, month, page, size));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('social:record')")
    public Result<Integer> generate(
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestParam(required = false) List<Long> lawyerIds) {
        return Result.success(recordService.generateRecords(year, month, lawyerIds));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('social:record')")
    public Result<Void> delete(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return Result.success();
    }
}
