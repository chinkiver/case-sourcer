package com.lawfirm.caseledger.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.caseledger.common.BusinessException;
import com.lawfirm.caseledger.dto.SocialInsuranceConfigDto;
import com.lawfirm.caseledger.dto.SocialInsuranceConfigRequest;
import com.lawfirm.caseledger.entity.SocialInsuranceConfig;
import com.lawfirm.caseledger.mapper.SocialInsuranceConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialInsuranceConfigService {

    private final SocialInsuranceConfigMapper configMapper;

    public Page<SocialInsuranceConfigDto> pageConfigs(Integer year, Integer month, Integer page, Integer size) {
        QueryWrapper<SocialInsuranceConfig> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        if (year != null) {
            wrapper.eq("year", year);
        }
        if (month != null) {
            wrapper.eq("month", month);
        }
        wrapper.orderByDesc("year", "month");
        Page<SocialInsuranceConfig> configPage = configMapper.selectPage(new Page<>(page, size), wrapper);
        List<SocialInsuranceConfigDto> records = configPage.getRecords().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        Page<SocialInsuranceConfigDto> result = new Page<>(configPage.getCurrent(), configPage.getSize(), configPage.getTotal());
        result.setRecords(records);
        return result;
    }

    public SocialInsuranceConfigDto getConfig(Long id) {
        SocialInsuranceConfig config = configMapper.selectById(id);
        if (config == null || config.getDeleted() == 1) {
            throw new BusinessException("配置不存在");
        }
        return convertToDto(config);
    }

    public SocialInsuranceConfig getConfigByMonth(Integer year, Integer month) {
        QueryWrapper<SocialInsuranceConfig> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0).eq("year", year).eq("month", month);
        return configMapper.selectOne(wrapper);
    }

    @Transactional
    public SocialInsuranceConfigDto createConfig(SocialInsuranceConfigRequest request) {
        if (existsByMonth(request.getYear(), request.getMonth())) {
            throw new BusinessException(request.getYear() + "年" + request.getMonth() + "月的配置已存在");
        }
        SocialInsuranceConfig config = new SocialInsuranceConfig();
        BeanUtils.copyProperties(request, config);
        fillDefaultRates(config);
        configMapper.insert(config);
        return convertToDto(config);
    }

    @Transactional
    public SocialInsuranceConfigDto updateConfig(Long id, SocialInsuranceConfigRequest request) {
        SocialInsuranceConfig config = configMapper.selectById(id);
        if (config == null || config.getDeleted() == 1) {
            throw new BusinessException("配置不存在");
        }
        Integer originalYear = config.getYear();
        Integer originalMonth = config.getMonth();
        BeanUtils.copyProperties(request, config);
        fillDefaultRates(config);
        if (!originalYear.equals(config.getYear()) || !originalMonth.equals(config.getMonth())) {
            if (existsByMonth(config.getYear(), config.getMonth(), id)) {
                throw new BusinessException(config.getYear() + "年" + config.getMonth() + "月的配置已存在");
            }
        }
        configMapper.updateById(config);
        return convertToDto(config);
    }

    @Transactional
    public void deleteConfig(Long id) {
        SocialInsuranceConfig config = configMapper.selectById(id);
        if (config == null || config.getDeleted() == 1) {
            throw new BusinessException("配置不存在");
        }
        configMapper.deleteById(id);
    }

    private boolean existsByMonth(Integer year, Integer month) {
        QueryWrapper<SocialInsuranceConfig> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0).eq("year", year).eq("month", month);
        return configMapper.selectCount(wrapper) > 0;
    }

    private boolean existsByMonth(Integer year, Integer month, Long excludeId) {
        QueryWrapper<SocialInsuranceConfig> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0).eq("year", year).eq("month", month).ne("id", excludeId);
        return configMapper.selectCount(wrapper) > 0;
    }

    private void fillDefaultRates(SocialInsuranceConfig config) {
        if (config.getPensionRatePersonal() == null) config.setPensionRatePersonal(new java.math.BigDecimal("0.0800"));
        if (config.getPensionRateCompany() == null) config.setPensionRateCompany(new java.math.BigDecimal("0.1600"));
        if (config.getMedicalRatePersonal() == null) config.setMedicalRatePersonal(new java.math.BigDecimal("0.0200"));
        if (config.getMedicalRateCompany() == null) config.setMedicalRateCompany(new java.math.BigDecimal("0.0900"));
        if (config.getUnemploymentRatePersonal() == null) config.setUnemploymentRatePersonal(new java.math.BigDecimal("0.0050"));
        if (config.getUnemploymentRateCompany() == null) config.setUnemploymentRateCompany(new java.math.BigDecimal("0.0050"));
        if (config.getInjuryRateCompany() == null) config.setInjuryRateCompany(new java.math.BigDecimal("0.0050"));
        if (config.getMaternityRateCompany() == null) config.setMaternityRateCompany(new java.math.BigDecimal("0.0080"));
        if (config.getHousingRatePersonal() == null) config.setHousingRatePersonal(new java.math.BigDecimal("0.0500"));
        if (config.getHousingRateCompany() == null) config.setHousingRateCompany(new java.math.BigDecimal("0.0500"));
    }

    private SocialInsuranceConfigDto convertToDto(SocialInsuranceConfig config) {
        SocialInsuranceConfigDto dto = new SocialInsuranceConfigDto();
        BeanUtils.copyProperties(config, dto);
        return dto;
    }
}
