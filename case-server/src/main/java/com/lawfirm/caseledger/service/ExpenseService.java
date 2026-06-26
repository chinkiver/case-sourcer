package com.lawfirm.caseledger.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.caseledger.common.BusinessException;
import com.lawfirm.caseledger.dto.ExpenseDto;
import com.lawfirm.caseledger.dto.ExpenseRequest;
import com.lawfirm.caseledger.entity.AccountTransaction;
import com.lawfirm.caseledger.entity.Expense;
import com.lawfirm.caseledger.entity.Lawyer;
import com.lawfirm.caseledger.mapper.AccountTransactionMapper;
import com.lawfirm.caseledger.mapper.ExpenseMapper;
import com.lawfirm.caseledger.mapper.LawyerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseMapper expenseMapper;
    private final LawyerMapper lawyerMapper;
    private final AccountTransactionMapper accountTransactionMapper;

    public Page<ExpenseDto> pageExpenses(Long lawyerId, String expenseType, String expenseMonth,
                                          Integer page, Integer size) {
        QueryWrapper<Expense> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        if (lawyerId != null) {
            wrapper.eq("lawyer_id", lawyerId);
        }
        if (expenseType != null && !expenseType.isBlank()) {
            wrapper.eq("expense_type", expenseType);
        }
        if (expenseMonth != null && !expenseMonth.isBlank()) {
            wrapper.eq("expense_month", expenseMonth);
        }
        wrapper.orderByDesc("expense_date", "create_time");
        Page<Expense> expensePage = expenseMapper.selectPage(new Page<>(page, size), wrapper);
        List<ExpenseDto> records = expensePage.getRecords().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        Page<ExpenseDto> result = new Page<>(expensePage.getCurrent(), expensePage.getSize(), expensePage.getTotal());
        result.setRecords(records);
        return result;
    }

    @Transactional
    public ExpenseDto createExpense(ExpenseRequest request) {
        Lawyer lawyer = lawyerMapper.selectById(request.getLawyerId());
        if (lawyer == null || lawyer.getDeleted() == 1) {
            throw new BusinessException("律师不存在");
        }
        Expense expense = new Expense();
        BeanUtils.copyProperties(request, expense);
        expense.setExpenseMonth(request.getExpenseDate().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        expense.setAmount(request.getAmount().abs());
        expenseMapper.insert(expense);

        addTransaction(lawyer.getId(), expense.getExpenseDate(), expense.getExpenseMonth(),
                expense.getExpenseType(), expense.getAmount().negate(), expense.getRemark());

        return convertToDto(expense);
    }

    @Transactional
    public void deleteExpense(Long id) {
        Expense expense = expenseMapper.selectById(id);
        if (expense == null || expense.getDeleted() == 1) {
            throw new BusinessException("支出记录不存在");
        }
        deleteRelatedTransaction(expense);
        expenseMapper.deleteById(id);
        recomputeBalances(expense.getLawyerId());
    }

    private void deleteRelatedTransaction(Expense expense) {
        QueryWrapper<AccountTransaction> wrapper = new QueryWrapper<>();
        wrapper.eq("lawyer_id", expense.getLawyerId())
                .eq("txn_type", expense.getExpenseType())
                .eq("txn_date", expense.getExpenseDate())
                .eq("amount", expense.getAmount().negate())
                .eq("deleted", 0)
                .orderByDesc("create_time")
                .last("LIMIT 1");
        AccountTransaction txn = accountTransactionMapper.selectOne(wrapper);
        if (txn != null) {
            accountTransactionMapper.deleteById(txn.getId());
        }
    }

    private void recomputeBalances(Long lawyerId) {
        QueryWrapper<AccountTransaction> wrapper = new QueryWrapper<>();
        wrapper.eq("lawyer_id", lawyerId).eq("deleted", 0).orderByAsc("create_time");
        List<AccountTransaction> txns = accountTransactionMapper.selectList(wrapper);
        if (txns.isEmpty()) {
            return;
        }
        BigDecimal balance = BigDecimal.ZERO;
        Lawyer lawyer = lawyerMapper.selectById(lawyerId);
        if (lawyer != null && lawyer.getOpeningBalance() != null) {
            balance = lawyer.getOpeningBalance();
        }
        for (AccountTransaction txn : txns) {
            balance = balance.add(txn.getAmount());
            txn.setBalance(balance);
            accountTransactionMapper.updateById(txn);
        }
    }

    private void addTransaction(Long lawyerId, LocalDate date, String month, String type,
                                BigDecimal amount, String remark) {
        BigDecimal lastBalance = getLastBalance(lawyerId);
        BigDecimal newBalance = lastBalance.add(amount);

        AccountTransaction txn = new AccountTransaction();
        txn.setLawyerId(lawyerId);
        txn.setTxnDate(date);
        txn.setTxnMonth(month);
        txn.setTxnType(type);
        txn.setAmount(amount);
        txn.setBalance(newBalance);
        txn.setRemark(remark);
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

    private ExpenseDto convertToDto(Expense expense) {
        ExpenseDto dto = new ExpenseDto();
        BeanUtils.copyProperties(expense, dto);
        Lawyer lawyer = lawyerMapper.selectById(expense.getLawyerId());
        if (lawyer != null) {
            dto.setLawyerName(lawyer.getName());
        }
        return dto;
    }
}
