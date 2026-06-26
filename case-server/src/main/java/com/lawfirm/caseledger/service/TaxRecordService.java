package com.lawfirm.caseledger.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.caseledger.common.BusinessException;
import com.lawfirm.caseledger.dto.TaxBracket;
import com.lawfirm.caseledger.dto.TaxRecordDto;
import com.lawfirm.caseledger.entity.AccountTransaction;
import com.lawfirm.caseledger.entity.Lawyer;
import com.lawfirm.caseledger.entity.SocialInsuranceRecord;
import com.lawfirm.caseledger.entity.SysConfig;
import com.lawfirm.caseledger.entity.TaxRecord;
import com.lawfirm.caseledger.mapper.AccountTransactionMapper;
import com.lawfirm.caseledger.mapper.LawyerMapper;
import com.lawfirm.caseledger.mapper.SocialInsuranceRecordMapper;
import com.lawfirm.caseledger.mapper.SysConfigMapper;
import com.lawfirm.caseledger.mapper.TaxRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaxRecordService {

    private final TaxRecordMapper taxRecordMapper;
    private final LawyerMapper lawyerMapper;
    private final AccountTransactionMapper accountTransactionMapper;
    private final SocialInsuranceRecordMapper socialInsuranceRecordMapper;
    private final SysConfigMapper sysConfigMapper;

    private static final List<TaxBracket> BRACKETS = new ArrayList<>();

    static {
        BRACKETS.add(new TaxBracket(new BigDecimal("36000"), new BigDecimal("0.03"), BigDecimal.ZERO));
        BRACKETS.add(new TaxBracket(new BigDecimal("144000"), new BigDecimal("0.10"), new BigDecimal("2520")));
        BRACKETS.add(new TaxBracket(new BigDecimal("300000"), new BigDecimal("0.20"), new BigDecimal("16920")));
        BRACKETS.add(new TaxBracket(new BigDecimal("420000"), new BigDecimal("0.25"), new BigDecimal("31920")));
        BRACKETS.add(new TaxBracket(new BigDecimal("660000"), new BigDecimal("0.30"), new BigDecimal("52920")));
        BRACKETS.add(new TaxBracket(new BigDecimal("960000"), new BigDecimal("0.35"), new BigDecimal("85920")));
        BRACKETS.add(new TaxBracket(new BigDecimal("99999999999"), new BigDecimal("0.45"), new BigDecimal("181920")));
    }

    public Page<TaxRecordDto> pageRecords(Long lawyerId, Integer year, Integer month, Integer page, Integer size) {
        QueryWrapper<TaxRecord> wrapper = new QueryWrapper<>();
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
        Page<TaxRecord> recordPage = taxRecordMapper.selectPage(new Page<>(page, size), wrapper);
        List<TaxRecordDto> records = recordPage.getRecords().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        Page<TaxRecordDto> result = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        result.setRecords(records);
        return result;
    }

    @Transactional
    public int generateRecords(Integer year, Integer month, List<Long> lawyerIds,
                               BigDecimal additionalDeduction, BigDecimal otherDeduction) {
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

        QueryWrapper<TaxRecord> existsWrapper = new QueryWrapper<>();
        existsWrapper.eq("deleted", 0).eq("year", year).eq("month", month)
                .in("lawyer_id", lawyers.stream().map(Lawyer::getId).collect(Collectors.toList()));
        if (taxRecordMapper.selectCount(existsWrapper) > 0) {
            throw new BusinessException(year + "年" + month + "月已存在个税扣缴记录，请先删除后重新生成");
        }

        BigDecimal threshold = getThreshold();
        LocalDate txnDate = LocalDate.of(year, month, 1);
        String txnMonth = txnDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        BigDecimal monthAdditional = additionalDeduction != null ? additionalDeduction : BigDecimal.ZERO;
        BigDecimal monthOther = otherDeduction != null ? otherDeduction : BigDecimal.ZERO;

        int count = 0;
        for (Lawyer lawyer : lawyers) {
            TaxRecord record = calculateTax(lawyer, year, month, threshold, monthAdditional, monthOther);
            if (record.getIncomeAmount().compareTo(BigDecimal.ZERO) > 0 || record.getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
                taxRecordMapper.insert(record);
                if (record.getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
                    addTransaction(lawyer.getId(), txnDate, txnMonth, record.getTaxAmount().negate());
                }
                count++;
            }
        }
        return count;
    }

    @Transactional
    public void deleteRecord(Long id) {
        TaxRecord record = taxRecordMapper.selectById(id);
        if (record == null || record.getDeleted() == 1) {
            throw new BusinessException("记录不存在");
        }
        taxRecordMapper.deleteById(id);
    }

    private TaxRecord calculateTax(Lawyer lawyer, Integer year, Integer month, BigDecimal threshold,
                                   BigDecimal monthAdditional, BigDecimal monthOther) {
        String txnMonth = String.format("%d-%02d", year, month);
        BigDecimal monthlyIncome = getMonthlyIncome(lawyer.getId(), txnMonth);
        BigDecimal monthlySocial = getMonthlySocialInsurance(lawyer.getId(), year, month);

        BigDecimal cumulativeIncome = getCumulativeIncome(lawyer.getId(), year, month);
        BigDecimal cumulativeSocial = getCumulativeSocialInsurance(lawyer.getId(), year, month);
        BigDecimal cumulativeAdditional = getCumulativeDeduction(lawyer.getId(), year, month, "additional_deduction");
        cumulativeAdditional = cumulativeAdditional.add(monthAdditional);
        BigDecimal cumulativeOther = getCumulativeDeduction(lawyer.getId(), year, month, "other_deduction");
        cumulativeOther = cumulativeOther.add(monthOther);

        BigDecimal cumulativeThreshold = threshold.multiply(new BigDecimal(month));
        BigDecimal cumulativeTaxable = cumulativeIncome
                .subtract(cumulativeSocial)
                .subtract(cumulativeAdditional)
                .subtract(cumulativeOther)
                .subtract(cumulativeThreshold);
        if (cumulativeTaxable.compareTo(BigDecimal.ZERO) < 0) {
            cumulativeTaxable = BigDecimal.ZERO;
        }

        TaxBracket bracket = findBracket(cumulativeTaxable);
        BigDecimal cumulativeTax = cumulativeTaxable.multiply(bracket.getRate()).subtract(bracket.getQuickDeduction())
                .setScale(2, RoundingMode.HALF_UP);
        if (cumulativeTax.compareTo(BigDecimal.ZERO) < 0) {
            cumulativeTax = BigDecimal.ZERO;
        }

        BigDecimal previousCumulativeTax = getPreviousCumulativeTax(lawyer.getId(), year, month);
        BigDecimal currentTax = cumulativeTax.subtract(previousCumulativeTax);
        if (currentTax.compareTo(BigDecimal.ZERO) < 0) {
            currentTax = BigDecimal.ZERO;
        }

        TaxRecord record = new TaxRecord();
        record.setLawyerId(lawyer.getId());
        record.setYear(year);
        record.setMonth(month);
        record.setIncomeAmount(monthlyIncome);
        record.setDeductibleAmount(threshold);
        record.setSpecialDeduction(monthlySocial);
        record.setAdditionalDeduction(monthAdditional);
        record.setOtherDeduction(monthOther);
        record.setTaxableIncome(cumulativeTaxable);
        record.setTaxRate(bracket.getRate());
        record.setQuickDeduction(bracket.getQuickDeduction());
        record.setTaxAmount(currentTax);
        record.setPaidTax(previousCumulativeTax);
        record.setCumulativeIncome(cumulativeIncome);
        record.setCumulativeTaxable(cumulativeTaxable);
        record.setCumulativeTax(cumulativeTax);
        return record;
    }

    private BigDecimal getMonthlyIncome(Long lawyerId, String txnMonth) {
        QueryWrapper<AccountTransaction> wrapper = new QueryWrapper<>();
        wrapper.eq("lawyer_id", lawyerId)
                .eq("deleted", 0)
                .eq("txn_month", txnMonth)
                .eq("txn_type", "账户增加");
        List<AccountTransaction> list = accountTransactionMapper.selectList(wrapper);
        return list.stream().map(AccountTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getCumulativeIncome(Long lawyerId, Integer year, Integer month) {
        BigDecimal sum = BigDecimal.ZERO;
        for (int m = 1; m <= month; m++) {
            sum = sum.add(getMonthlyIncome(lawyerId, String.format("%d-%02d", year, m)));
        }
        return sum;
    }

    private BigDecimal getMonthlySocialInsurance(Long lawyerId, Integer year, Integer month) {
        QueryWrapper<SocialInsuranceRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("lawyer_id", lawyerId).eq("year", year).eq("month", month).eq("deleted", 0);
        SocialInsuranceRecord record = socialInsuranceRecordMapper.selectOne(wrapper);
        return record != null && record.getTotalPersonal() != null ? record.getTotalPersonal() : BigDecimal.ZERO;
    }

    private BigDecimal getCumulativeSocialInsurance(Long lawyerId, Integer year, Integer month) {
        BigDecimal sum = BigDecimal.ZERO;
        for (int m = 1; m <= month; m++) {
            sum = sum.add(getMonthlySocialInsurance(lawyerId, year, m));
        }
        return sum;
    }

    private BigDecimal getCumulativeDeduction(Long lawyerId, Integer year, Integer month, String field) {
        if (month <= 1) {
            return BigDecimal.ZERO;
        }
        QueryWrapper<TaxRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("lawyer_id", lawyerId).eq("year", year).lt("month", month).eq("deleted", 0);
        List<TaxRecord> records = taxRecordMapper.selectList(wrapper);
        return records.stream()
                .map(r -> {
                    if ("additional_deduction".equals(field)) {
                        return r.getAdditionalDeduction() != null ? r.getAdditionalDeduction() : BigDecimal.ZERO;
                    } else {
                        return r.getOtherDeduction() != null ? r.getOtherDeduction() : BigDecimal.ZERO;
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getPreviousCumulativeTax(Long lawyerId, Integer year, Integer month) {
        if (month <= 1) {
            return BigDecimal.ZERO;
        }
        QueryWrapper<TaxRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("lawyer_id", lawyerId).eq("year", year).eq("month", month - 1).eq("deleted", 0);
        TaxRecord last = taxRecordMapper.selectOne(wrapper);
        return last != null && last.getCumulativeTax() != null ? last.getCumulativeTax() : BigDecimal.ZERO;
    }

    private TaxBracket findBracket(BigDecimal taxableIncome) {
        for (TaxBracket bracket : BRACKETS) {
            if (taxableIncome.compareTo(bracket.getLimit()) <= 0) {
                return bracket;
            }
        }
        return BRACKETS.get(BRACKETS.size() - 1);
    }

    private BigDecimal getThreshold() {
        QueryWrapper<SysConfig> wrapper = new QueryWrapper<>();
        wrapper.eq("config_key", "ledger.tax.threshold");
        SysConfig config = sysConfigMapper.selectOne(wrapper);
        if (config != null && config.getConfigValue() != null) {
            try {
                return new BigDecimal(config.getConfigValue());
            } catch (NumberFormatException ignored) {
            }
        }
        return new BigDecimal("5000");
    }

    private void addTransaction(Long lawyerId, LocalDate date, String month, BigDecimal amount) {
        BigDecimal lastBalance = getLastBalance(lawyerId);
        BigDecimal newBalance = lastBalance.add(amount);

        AccountTransaction txn = new AccountTransaction();
        txn.setLawyerId(lawyerId);
        txn.setTxnDate(date);
        txn.setTxnMonth(month);
        txn.setTxnType("个税扣缴");
        txn.setAmount(amount);
        txn.setBalance(newBalance);
        txn.setRemark(month + " 个税扣缴");
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

    private TaxRecordDto convertToDto(TaxRecord record) {
        TaxRecordDto dto = new TaxRecordDto();
        BeanUtils.copyProperties(record, dto);
        Lawyer lawyer = lawyerMapper.selectById(record.getLawyerId());
        if (lawyer != null) {
            dto.setLawyerName(lawyer.getName());
        }
        return dto;
    }
}
