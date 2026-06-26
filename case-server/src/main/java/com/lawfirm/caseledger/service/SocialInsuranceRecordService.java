package com.lawfirm.caseledger.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.caseledger.common.BusinessException;
import com.lawfirm.caseledger.dto.SocialInsuranceRecordDto;
import com.lawfirm.caseledger.entity.AccountTransaction;
import com.lawfirm.caseledger.entity.Lawyer;
import com.lawfirm.caseledger.entity.SocialInsuranceConfig;
import com.lawfirm.caseledger.entity.SocialInsuranceRecord;
import com.lawfirm.caseledger.mapper.AccountTransactionMapper;
import com.lawfirm.caseledger.mapper.LawyerMapper;
import com.lawfirm.caseledger.mapper.SocialInsuranceRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialInsuranceRecordService {

    private final SocialInsuranceRecordMapper recordMapper;
    private final SocialInsuranceConfigService configService;
    private final LawyerMapper lawyerMapper;
    private final AccountTransactionMapper accountTransactionMapper;

    public Page<SocialInsuranceRecordDto> pageRecords(Long lawyerId, Integer year, Integer month,
                                                       Integer page, Integer size) {
        QueryWrapper<SocialInsuranceRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        if (lawyerId != null) {
            wrapper.eq("lawyer_id", lawyerId);
        }
        if (year != null) {
            wrapper.eq("year", year);
        }
        if (month != null) {
            wrapper.eq("month", month);
        }
        wrapper.orderByDesc("year", "month", "create_time");
        Page<SocialInsuranceRecord> recordPage = recordMapper.selectPage(new Page<>(page, size), wrapper);
        List<SocialInsuranceRecordDto> records = recordPage.getRecords().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        Page<SocialInsuranceRecordDto> result = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        result.setRecords(records);
        return result;
    }

    @Transactional
    public int generateRecords(Integer year, Integer month, List<Long> lawyerIds) {
        SocialInsuranceConfig config = configService.getConfigByMonth(year, month);
        if (config == null) {
            throw new BusinessException(year + "年" + month + "月的社保公积金配置不存在");
        }

        List<Lawyer> lawyers;
        if (lawyerIds == null || lawyerIds.isEmpty()) {
            QueryWrapper<Lawyer> wrapper = new QueryWrapper<>();
            wrapper.eq("status", 1).eq("deleted", 0);
            lawyers = lawyerMapper.selectList(wrapper);
        } else {
            lawyers = lawyerMapper.selectBatchIds(lawyerIds).stream()
                    .filter(l -> l.getStatus() != null && l.getStatus() == 1 && l.getDeleted() == 0)
                    .collect(Collectors.toList());
        }

        if (lawyers.isEmpty()) {
            return 0;
        }

        // 检查是否已存在记录
        QueryWrapper<SocialInsuranceRecord> existsWrapper = new QueryWrapper<>();
        existsWrapper.eq("deleted", 0).eq("year", year).eq("month", month)
                .in("lawyer_id", lawyers.stream().map(Lawyer::getId).collect(Collectors.toList()));
        if (recordMapper.selectCount(existsWrapper) > 0) {
            throw new BusinessException(year + "年" + month + "月已存在社保公积金扣缴记录，请先删除后重新生成");
        }

        LocalDate txnDate = LocalDate.of(year, month, 1);
        String txnMonth = txnDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        int count = 0;
        for (Lawyer lawyer : lawyers) {
            SocialInsuranceRecord record = calculateRecord(config, lawyer, year, month);
            recordMapper.insert(record);

            if (record.getTotalPersonal().compareTo(BigDecimal.ZERO) > 0) {
                addTransaction(lawyer.getId(), txnDate, txnMonth, record.getTotalPersonal().negate());
            }
            count++;
        }
        return count;
    }

    @Transactional
    public void deleteRecord(Long id) {
        SocialInsuranceRecord record = recordMapper.selectById(id);
        if (record == null || record.getDeleted() == 1) {
            throw new BusinessException("记录不存在");
        }
        recordMapper.deleteById(id);
    }

    private SocialInsuranceRecord calculateRecord(SocialInsuranceConfig config, Lawyer lawyer, Integer year, Integer month) {
        BigDecimal base = config.getBaseAmount();
        SocialInsuranceRecord record = new SocialInsuranceRecord();
        record.setLawyerId(lawyer.getId());
        record.setYear(year);
        record.setMonth(month);
        record.setBaseAmount(base);

        record.setPensionPersonal(multiply(base, config.getPensionRatePersonal()));
        record.setPensionCompany(multiply(base, config.getPensionRateCompany()));
        record.setMedicalPersonal(multiply(base, config.getMedicalRatePersonal()));
        record.setMedicalCompany(multiply(base, config.getMedicalRateCompany()));
        record.setUnemploymentPersonal(multiply(base, config.getUnemploymentRatePersonal()));
        record.setUnemploymentCompany(multiply(base, config.getUnemploymentRateCompany()));
        record.setInjuryCompany(multiply(base, config.getInjuryRateCompany()));
        record.setMaternityCompany(multiply(base, config.getMaternityRateCompany()));
        record.setHousingPersonal(multiply(base, config.getHousingRatePersonal()));
        record.setHousingCompany(multiply(base, config.getHousingRateCompany()));

        BigDecimal totalPersonal = record.getPensionPersonal()
                .add(record.getMedicalPersonal())
                .add(record.getUnemploymentPersonal())
                .add(record.getHousingPersonal());
        BigDecimal totalCompany = record.getPensionCompany()
                .add(record.getMedicalCompany())
                .add(record.getUnemploymentCompany())
                .add(record.getInjuryCompany())
                .add(record.getMaternityCompany())
                .add(record.getHousingCompany());
        record.setTotalPersonal(totalPersonal);
        record.setTotalCompany(totalCompany);
        return record;
    }

    private BigDecimal multiply(BigDecimal base, BigDecimal rate) {
        if (base == null || rate == null) {
            return BigDecimal.ZERO;
        }
        return base.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    private void addTransaction(Long lawyerId, LocalDate date, String month, BigDecimal amount) {
        BigDecimal lastBalance = getLastBalance(lawyerId);
        BigDecimal newBalance = lastBalance.add(amount);

        AccountTransaction txn = new AccountTransaction();
        txn.setLawyerId(lawyerId);
        txn.setTxnDate(date);
        txn.setTxnMonth(month);
        txn.setTxnType("社保公积金扣款");
        txn.setAmount(amount);
        txn.setBalance(newBalance);
        txn.setRemark(month + " 社保公积金个人部分扣款");
        accountTransactionMapper.insert(txn);
    }

    private BigDecimal getLastBalance(Long lawyerId) {
        QueryWrapper<AccountTransaction> wrapper = new QueryWrapper<>();
        wrapper.eq("lawyer_id", lawyerId)
                .eq("deleted", 0)
                .orderByDesc("create_time")
                .last("LIMIT 1");
        AccountTransaction last = accountTransactionMapper.selectOne(wrapper);
        if (last != null) {
            return last.getBalance();
        }
        Lawyer lawyer = lawyerMapper.selectById(lawyerId);
        return lawyer != null && lawyer.getOpeningBalance() != null ? lawyer.getOpeningBalance() : BigDecimal.ZERO;
    }

    private SocialInsuranceRecordDto convertToDto(SocialInsuranceRecord record) {
        SocialInsuranceRecordDto dto = new SocialInsuranceRecordDto();
        BeanUtils.copyProperties(record, dto);
        Lawyer lawyer = lawyerMapper.selectById(record.getLawyerId());
        if (lawyer != null) {
            dto.setLawyerName(lawyer.getName());
        }
        return dto;
    }
}
