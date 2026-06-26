package com.lawfirm.caseledger.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.caseledger.common.BusinessException;
import com.lawfirm.caseledger.dto.ProjectDto;
import com.lawfirm.caseledger.dto.ProjectRequest;
import com.lawfirm.caseledger.entity.Client;
import com.lawfirm.caseledger.entity.Lawyer;
import com.lawfirm.caseledger.entity.Project;
import com.lawfirm.caseledger.mapper.ClientMapper;
import com.lawfirm.caseledger.mapper.LawyerMapper;
import com.lawfirm.caseledger.mapper.ProjectMapper;
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
public class ProjectService {

    private final ProjectMapper projectMapper;
    private final ClientMapper clientMapper;
    private final LawyerMapper lawyerMapper;

    @Transactional
    public ProjectDto createProject(ProjectRequest request) {
        Project project = new Project();
        BeanUtils.copyProperties(request, project);
        if (request.getProjectNo() == null || request.getProjectNo().isBlank()) {
            project.setProjectNo(generateProjectNo());
        }
        project.setReceivedAmount(BigDecimal.ZERO);
        project.setReceiveRatio(BigDecimal.ZERO);
        project.setArchiveStatus(0);
        project.setCaseStatus(0);
        projectMapper.insert(project);
        return convertToDto(project);
    }

    @Transactional
    public ProjectDto updateProject(Long id, ProjectRequest request) {
        Project project = projectMapper.selectById(id);
        if (project == null || project.getDeleted() == 1) {
            throw new BusinessException("项目不存在");
        }
        BeanUtils.copyProperties(request, project);
        projectMapper.updateById(project);
        return convertToDto(project);
    }

    public ProjectDto getProject(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null || project.getDeleted() == 1) {
            throw new BusinessException("项目不存在");
        }
        return convertToDto(project);
    }

    public Page<ProjectDto> pageProjects(String keyword, Long lawyerId, Integer page, Integer size) {
        QueryWrapper<Project> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("project_name", keyword)
                    .or()
                    .like("project_no", keyword));
        }
        if (lawyerId != null) {
            wrapper.and(w -> w.eq("host_lawyer_id", lawyerId)
                    .or()
                    .eq("assist_lawyer_id", lawyerId));
        }
        wrapper.orderByDesc("create_time");
        Page<Project> projectPage = projectMapper.selectPage(new Page<>(page, size), wrapper);
        List<ProjectDto> records = projectPage.getRecords().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        Page<ProjectDto> result = new Page<>(projectPage.getCurrent(), projectPage.getSize(), projectPage.getTotal());
        result.setRecords(records);
        return result;
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null || project.getDeleted() == 1) {
            throw new BusinessException("项目不存在");
        }
        projectMapper.deleteById(id);
    }

    private String generateProjectNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        QueryWrapper<Project> wrapper = new QueryWrapper<>();
        wrapper.like("project_no", date + "%");
        long count = projectMapper.selectCount(wrapper);
        return "MS" + date + String.format("%03d", count + 1);
    }

    private ProjectDto convertToDto(Project project) {
        ProjectDto dto = new ProjectDto();
        BeanUtils.copyProperties(project, dto);
        if (project.getClientId() != null) {
            Client client = clientMapper.selectById(project.getClientId());
            if (client != null) {
                dto.setClientName(client.getName());
            }
        }
        if (project.getPartyId() != null) {
            Client party = clientMapper.selectById(project.getPartyId());
            if (party != null) {
                dto.setPartyName(party.getName());
            }
        }
        if (project.getHostLawyerId() != null) {
            Lawyer lawyer = lawyerMapper.selectById(project.getHostLawyerId());
            if (lawyer != null) {
                dto.setHostLawyerName(lawyer.getName());
            }
        }
        if (project.getAssistLawyerId() != null) {
            Lawyer lawyer = lawyerMapper.selectById(project.getAssistLawyerId());
            if (lawyer != null) {
                dto.setAssistLawyerName(lawyer.getName());
            }
        }
        if (project.getContractAmount() != null && project.getContractAmount().compareTo(BigDecimal.ZERO) > 0) {
            dto.setReceiveRatio(project.getReceivedAmount()
                    .multiply(new BigDecimal("100"))
                    .divide(project.getContractAmount(), 2, RoundingMode.HALF_UP));
        }
        return dto;
    }
}
