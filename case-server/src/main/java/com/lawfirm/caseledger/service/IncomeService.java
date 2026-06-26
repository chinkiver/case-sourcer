package com.lawfirm.caseledger.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lawfirm.caseledger.common.BusinessException;
import com.lawfirm.caseledger.dto.ProjectIncomeDto;
import com.lawfirm.caseledger.dto.ProjectIncomeRequest;
import com.lawfirm.caseledger.entity.AccountTransaction;
import com.lawfirm.caseledger.entity.Lawyer;
import com.lawfirm.caseledger.entity.Project;
import com.lawfirm.caseledger.entity.ProjectIncome;
import com.lawfirm.caseledger.mapper.AccountTransactionMapper;
import com.lawfirm.caseledger.mapper.LawyerMapper;
import com.lawfirm.caseledger.mapper.ProjectIncomeMapper;
import com.lawfirm.caseledger.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final ProjectIncomeMapper projectIncomeMapper;
    private final ProjectMapper projectMapper;
    private final LawyerMapper lawyerMapper;
    private final AccountTransactionMapper accountTransactionMapper;

    @Transactional
    public ProjectIncomeDto createIncome(ProjectIncomeRequest request) {
        Project project = projectMapper.selectById(request.getProjectId());
        if (project == null || project.getDeleted() == 1) {
            throw new BusinessException("项目不存在");
        }

        ProjectIncome income = new ProjectIncome();
        BeanUtils.copyProperties(request, income);
        if (income.getHostAmount() == null) {
            income.setHostAmount(BigDecimal.ZERO);
        }
        if (income.getAssistAmount() == null) {
            income.setAssistAmount(BigDecimal.ZERO);
        }
        projectIncomeMapper.insert(income);

        // 更新项目回款金额
        BigDecimal newReceived = project.getReceivedAmount().add(income.getIncomeAmount());
        project.setReceivedAmount(newReceived);
        BigDecimal ratio = BigDecimal.ZERO;
        if (project.getContractAmount() != null && project.getContractAmount().compareTo(BigDecimal.ZERO) > 0) {
            ratio = newReceived.multiply(new BigDecimal("100"))
                    .divide(project.getContractAmount(), 2, RoundingMode.HALF_UP);
        }
        project.setReceiveRatio(ratio);
        projectMapper.updateById(project);

        // 生成账户流水
        String month = income.getIncomeDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        createIncomeTransactions(project, income, month);

        return convertToDto(income, project);
    }

    private void createIncomeTransactions(Project project, ProjectIncome income, String month) {
        // 主办律师流水
        if (project.getHostLawyerId() != null && income.getHostAmount().compareTo(BigDecimal.ZERO) > 0) {
            Lawyer hostLawyer = lawyerMapper.selectById(project.getHostLawyerId());
            if (hostLawyer != null) {
                BigDecimal commissionRate = hostLawyer.getCommissionRate() != null
                        ? hostLawyer.getCommissionRate()
                        : new BigDecimal("0.85");
                BigDecimal hostIncrease = income.getHostAmount().multiply(commissionRate)
                        .setScale(2, RoundingMode.HALF_UP);

                // 项目收款
                addTransaction(hostLawyer.getId(), income.getIncomeDate(), month, project.getId(), income.getId(),
                        "项目收款", income.getHostAmount(), "项目" + project.getProjectNo() + "收入");
                // 账户增加（提成）
                addTransaction(hostLawyer.getId(), income.getIncomeDate(), month, project.getId(), income.getId(),
                        "账户增加", hostIncrease, "项目" + project.getProjectNo() + "提成");
                // 暂扣档案费
                if (project.getArchiveFee() != null && project.getArchiveFee().compareTo(BigDecimal.ZERO) > 0) {
                    addTransaction(hostLawyer.getId(), income.getIncomeDate(), month, project.getId(), income.getId(),
                            "暂扣档案费", project.getArchiveFee().negate(), "项目" + project.getProjectNo() + "暂扣档案费");
                }
            }
        }

        // 协办律师流水
        if (project.getAssistLawyerId() != null && income.getAssistAmount().compareTo(BigDecimal.ZERO) > 0) {
            Lawyer assistLawyer = lawyerMapper.selectById(project.getAssistLawyerId());
            if (assistLawyer != null) {
                BigDecimal commissionRate = assistLawyer.getCommissionRate() != null
                        ? assistLawyer.getCommissionRate()
                        : new BigDecimal("0.85");
                BigDecimal assistIncrease = income.getAssistAmount().multiply(commissionRate)
                        .setScale(2, RoundingMode.HALF_UP);
                addTransaction(assistLawyer.getId(), income.getIncomeDate(), month, project.getId(), income.getId(),
                        "项目收款", income.getAssistAmount(), "项目" + project.getProjectNo() + "收入");
                addTransaction(assistLawyer.getId(), income.getIncomeDate(), month, project.getId(), income.getId(),
                        "账户增加", assistIncrease, "项目" + project.getProjectNo() + "提成");
            }
        }
    }

    private void addTransaction(Long lawyerId, java.time.LocalDate date, String month, Long projectId,
                                Long incomeId, String type, BigDecimal amount, String remark) {
        BigDecimal lastBalance = getLastBalance(lawyerId);
        BigDecimal newBalance = lastBalance.add(amount);

        AccountTransaction txn = new AccountTransaction();
        txn.setLawyerId(lawyerId);
        txn.setTxnDate(date);
        txn.setTxnMonth(month);
        txn.setProjectId(projectId);
        txn.setProjectIncomeId(incomeId);
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

    public List<ProjectIncomeDto> listByProject(Long projectId) {
        QueryWrapper<ProjectIncome> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId).eq("deleted", 0).orderByDesc("income_date");
        List<ProjectIncome> list = projectIncomeMapper.selectList(wrapper);
        Project project = projectMapper.selectById(projectId);
        return list.stream().map(i -> convertToDto(i, project)).collect(Collectors.toList());
    }

    private ProjectIncomeDto convertToDto(ProjectIncome income, Project project) {
        ProjectIncomeDto dto = new ProjectIncomeDto();
        BeanUtils.copyProperties(income, dto);
        if (project != null) {
            dto.setProjectName(project.getProjectName());
        }
        return dto;
    }
}
