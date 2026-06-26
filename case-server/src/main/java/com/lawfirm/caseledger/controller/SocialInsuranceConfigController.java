package com.lawfirm.caseledger.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.caseledger.common.Result;
import com.lawfirm.caseledger.dto.SocialInsuranceConfigDto;
import com.lawfirm.caseledger.dto.SocialInsuranceConfigRequest;
import com.lawfirm.caseledger.service.SocialInsuranceConfigService;
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
@RequestMapping("/api/social-insurance-configs")
@RequiredArgsConstructor
public class SocialInsuranceConfigController {

    private final SocialInsuranceConfigService configService;

    @GetMapping
    @PreAuthorize("hasAuthority('social:config') or hasAuthority('social:record')")
    public Result<Page<SocialInsuranceConfigDto>> page(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(configService.pageConfigs(year, month, page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('social:config')")
    public Result<SocialInsuranceConfigDto> get(@PathVariable Long id) {
        return Result.success(configService.getConfig(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('social:config')")
    public Result<SocialInsuranceConfigDto> create(@Valid @RequestBody SocialInsuranceConfigRequest request) {
        return Result.success(configService.createConfig(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('social:config')")
    public Result<SocialInsuranceConfigDto> update(@PathVariable Long id,
                                                   @Valid @RequestBody SocialInsuranceConfigRequest request) {
        return Result.success(configService.updateConfig(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('social:config')")
    public Result<Void> delete(@PathVariable Long id) {
        configService.deleteConfig(id);
        return Result.success();
    }
}
