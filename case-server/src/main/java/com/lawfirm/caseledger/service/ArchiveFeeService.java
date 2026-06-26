package com.lawfirm.caseledger.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.caseledger.common.BusinessException;
import com.lawfirm.caseledger.dto.ArchiveFeeDto;
import com.lawfirm.caseledger.dto.ArchiveFeeRequest;
import com.lawfirm.caseledger.entity.AccountTransaction;
import com.lawfirm.caseledger.entity.ArchiveFee;
import com.lawfirm.caseledger.entity.Lawyer;
import com.lawfirm.caseledger.entity.Project;
import com.lawfirm.caseledger.mapper.AccountTransactionMapper;
import com.lawfirm.caseledger.mapper.ArchiveFeeMapper;
import com.lawfirm.caseledger.mapper.LawyerMapper;
import com.lawfirm.caseledger.mapper.ProjectMapper;
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
public class ArchiveFeeService {

    private final ArchiveFeeMapper archiveFeeMapper;
    private final ProjectMapper projectMapper;
    private final LawyerMapper lawyerMapper;
    private final AccountTransactionMapper accountTransactionMapper;

    @Transactional
    public ArchiveFeeDto archiveProject(ArchiveFeeRequest request) {
        Project project = projectMapper.selectById(request.getProjectId());
        if (project == null || project.getDeleted() == 1) {
            throw new BusinessException("项目不存在");
        }
        if (project.getArchiveStatus() != null && project.getArchiveStatus() == 1) {
            throw new BusinessException("该项目已归档");
        }

        Lawyer lawyer = lawyerMapper.selectById(request.getLawyerId());
        if (lawyer == null || lawyer.getDeleted() == 1) {
            throw new BusinessException("律师不存在");
        }

        LocalDate archiveDate = request.getArchiveDate() != null ? request.getArchiveDate() : LocalDate.now();
        String month = archiveDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // 更新项目归档状态
        project.setArchiveStatus(1);
        projectMapper.updateById(project);

        // 创建档案费记录
        ArchiveFee archiveFee = new ArchiveFee();
        archiveFee.setProjectId(project.getId());
        archiveFee.setLawyerId(lawyer.getId());
        archiveFee.setFeeAmount(project.getArchiveFee());
        archiveFee.setArchiveStatus(1);
        archiveFee.setArchiveDate(archiveDate);
        archiveFee.setRefundStatus(0);
        archiveFee.setRemark(request.getRemark());
        archiveFeeMapper.insert(archiveFee);

        // 退还档案费到律师账户
        if (project.getArchiveFee() != null && project.getArchiveFee().compareTo(BigDecimal.ZERO) > 0) {
            addTransaction(lawyer.getId(), archiveDate, month, project.getId(),
                    "退还档案费", project.getArchiveFee(),
                    "项目" + project.getProjectNo() + "归档退还档案费");
        }

        return convertToDto(archiveFee, project, lawyer);
    }

    public Page<ArchiveFeeDto> pageArchiveFees(Long lawyerId, Integer archiveStatus, Integer page, Integer size) {
        QueryWrapper<ArchiveFee> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        if (lawyerId != null) {
            wrapper.eq("lawyer_id", lawyerId);
        }
        if (archiveStatus != null) {
            wrapper.eq("archive_status", archiveStatus);
        }
        wrapper.orderByDesc("create_time");
        Page<ArchiveFee> archivePage = archiveFeeMapper.selectPage(new Page<>(page, size), wrapper);
        List<ArchiveFeeDto> records = archivePage.getRecords().stream()
                .map(a -> convertToDto(a, null, null))
                .collect(Collectors.toList());
        Page<ArchiveFeeDto> result = new Page<>(archivePage.getCurrent(), archivePage.getSize(), archivePage.getTotal());
        result.setRecords(records);
        return result;
    }

    private void addTransaction(Long lawyerId, LocalDate date, String month, Long projectId,
                                String type, BigDecimal amount, String remark) {
        BigDecimal lastBalance = getLastBalance(lawyerId);
        BigDecimal newBalance = lastBalance.add(amount);

        AccountTransaction txn = new AccountTransaction();
        txn.setLawyerId(lawyerId);
        txn.setTxnDate(date);
        txn.setTxnMonth(month);
        txn.setProjectId(projectId);
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

    private ArchiveFeeDto convertToDto(ArchiveFee archiveFee, Project project, Lawyer lawyer) {
        ArchiveFeeDto dto = new ArchiveFeeDto();
        BeanUtils.copyProperties(archiveFee, dto);
        if (project == null && archiveFee.getProjectId() != null) {
            project = projectMapper.selectById(archiveFee.getProjectId());
        }
        if (project != null) {
            dto.setProjectNo(project.getProjectNo());
            dto.setProjectName(project.getProjectName());
        }
        if (lawyer == null && archiveFee.getLawyerId() != null) {
            lawyer = lawyerMapper.selectById(archiveFee.getLawyerId());
        }
        if (lawyer != null) {
            dto.setLawyerName(lawyer.getName());
        }
        return dto;
    }
}
