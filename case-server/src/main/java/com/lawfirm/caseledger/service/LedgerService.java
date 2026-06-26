package com.lawfirm.caseledger.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.caseledger.common.BusinessException;
import com.lawfirm.caseledger.dto.AccountSummaryDto;
import com.lawfirm.caseledger.dto.AccountTransactionDto;
import com.lawfirm.caseledger.entity.AccountTransaction;
import com.lawfirm.caseledger.entity.Lawyer;
import com.lawfirm.caseledger.entity.Project;
import com.lawfirm.caseledger.mapper.AccountTransactionMapper;
import com.lawfirm.caseledger.mapper.LawyerMapper;
import com.lawfirm.caseledger.mapper.ProjectMapper;
import com.lawfirm.caseledger.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final AccountTransactionMapper accountTransactionMapper;
    private final LawyerMapper lawyerMapper;
    private final ProjectMapper projectMapper;

    public Page<AccountTransactionDto> pageTransactions(Long lawyerId, String txnMonth, String txnType,
                                                        Integer page, Integer size, SecurityUser currentUser) {
        Long queryLawyerId = resolveLawyerId(lawyerId, currentUser);
        QueryWrapper<AccountTransaction> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        if (queryLawyerId != null) {
            wrapper.eq("lawyer_id", queryLawyerId);
        }
        if (txnMonth != null && !txnMonth.isEmpty()) {
            wrapper.eq("txn_month", txnMonth);
        }
        if (txnType != null && !txnType.isEmpty()) {
            wrapper.eq("txn_type", txnType);
        }
        wrapper.orderByDesc("txn_date", "create_time");
        Page<AccountTransaction> txnPage = accountTransactionMapper.selectPage(new Page<>(page, size), wrapper);
        List<AccountTransactionDto> records = txnPage.getRecords().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        Page<AccountTransactionDto> result = new Page<>(txnPage.getCurrent(), txnPage.getSize(), txnPage.getTotal());
        result.setRecords(records);
        return result;
    }

    public AccountSummaryDto getSummary(Long lawyerId, SecurityUser currentUser) {
        Long queryLawyerId = resolveLawyerId(lawyerId, currentUser);
        if (queryLawyerId == null) {
            throw new BusinessException("请选择律师");
        }
        Lawyer lawyer = lawyerMapper.selectById(queryLawyerId);
        if (lawyer == null || lawyer.getDeleted() == 1) {
            throw new BusinessException("律师不存在");
        }

        QueryWrapper<AccountTransaction> wrapper = new QueryWrapper<>();
        wrapper.eq("lawyer_id", queryLawyerId).eq("deleted", 0).orderByAsc("create_time");
        List<AccountTransaction> txns = accountTransactionMapper.selectList(wrapper);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        BigDecimal pendingArchiveFee = BigDecimal.ZERO;
        BigDecimal totalProjectIncome = BigDecimal.ZERO;
        BigDecimal totalCommission = BigDecimal.ZERO;
        BigDecimal totalSocialInsurance = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalPayout = BigDecimal.ZERO;
        for (AccountTransaction txn : txns) {
            if (txn.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                totalIncome = totalIncome.add(txn.getAmount());
            } else {
                totalExpense = totalExpense.add(txn.getAmount().abs());
            }
            String type = txn.getTxnType();
            if ("项目收款".equals(type)) {
                totalProjectIncome = totalProjectIncome.add(txn.getAmount());
            } else if ("账户增加".equals(type)) {
                totalCommission = totalCommission.add(txn.getAmount());
            } else if ("暂扣档案费".equals(type)) {
                pendingArchiveFee = pendingArchiveFee.add(txn.getAmount().abs());
            } else if ("社保公积金扣款".equals(type)) {
                totalSocialInsurance = totalSocialInsurance.add(txn.getAmount().abs());
            } else if ("个税扣缴".equals(type)) {
                totalTax = totalTax.add(txn.getAmount().abs());
            } else if (type != null && ("个人报销".equals(type) || "工资发放".equals(type) || "其他支出".equals(type))) {
                totalPayout = totalPayout.add(txn.getAmount().abs());
            }
        }

        BigDecimal currentBalance = txns.isEmpty()
                ? (lawyer.getOpeningBalance() != null ? lawyer.getOpeningBalance() : BigDecimal.ZERO)
                : txns.get(txns.size() - 1).getBalance();
        BigDecimal availableWithdrawal = currentBalance.subtract(pendingArchiveFee);
        if (availableWithdrawal.compareTo(BigDecimal.ZERO) < 0) {
            availableWithdrawal = BigDecimal.ZERO;
        }

        AccountSummaryDto summary = new AccountSummaryDto();
        summary.setLawyerId(lawyer.getId());
        summary.setLawyerName(lawyer.getName());
        summary.setOpeningBalance(lawyer.getOpeningBalance());
        summary.setTotalIncome(totalIncome);
        summary.setTotalExpense(totalExpense);
        summary.setCurrentBalance(currentBalance);
        summary.setPendingArchiveFee(pendingArchiveFee);
        summary.setTotalProjectIncome(totalProjectIncome);
        summary.setTotalCommission(totalCommission);
        summary.setTotalSocialInsurance(totalSocialInsurance);
        summary.setTotalTax(totalTax);
        summary.setTotalPayout(totalPayout);
        summary.setAvailableWithdrawal(availableWithdrawal);
        return summary;
    }

    private Long resolveLawyerId(Long lawyerId, SecurityUser currentUser) {
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("system") || a.getAuthority().equals("finance"));
        if (isAdmin) {
            return lawyerId;
        }
        // 普通律师只能查自己
        Lawyer lawyer = lawyerMapper.selectOne(
                new QueryWrapper<Lawyer>().eq("user_id", currentUser.getUserId()).eq("deleted", 0));
        if (lawyer == null) {
            throw new BusinessException("当前用户未关联律师信息");
        }
        if (lawyerId != null && !lawyerId.equals(lawyer.getId())) {
            throw new BusinessException("无权查看其他律师账户");
        }
        return lawyer.getId();
    }

    private AccountTransactionDto convertToDto(AccountTransaction txn) {
        AccountTransactionDto dto = new AccountTransactionDto();
        BeanUtils.copyProperties(txn, dto);
        Lawyer lawyer = lawyerMapper.selectById(txn.getLawyerId());
        if (lawyer != null) {
            dto.setLawyerName(lawyer.getName());
        }
        if (txn.getProjectId() != null) {
            Project project = projectMapper.selectById(txn.getProjectId());
            if (project != null) {
                dto.setProjectNo(project.getProjectNo());
                dto.setProjectName(project.getProjectName());
            }
        }
        return dto;
    }
}
