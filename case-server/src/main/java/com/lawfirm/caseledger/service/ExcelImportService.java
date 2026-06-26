package com.lawfirm.caseledger.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lawfirm.caseledger.common.BusinessException;
import com.lawfirm.caseledger.dto.ImportResult;
import com.lawfirm.caseledger.dto.ProjectIncomeRequest;
import com.lawfirm.caseledger.dto.ProjectRequest;
import com.lawfirm.caseledger.entity.Client;
import com.lawfirm.caseledger.entity.Lawyer;
import com.lawfirm.caseledger.entity.Project;
import com.lawfirm.caseledger.mapper.ClientMapper;
import com.lawfirm.caseledger.mapper.LawyerMapper;
import com.lawfirm.caseledger.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExcelImportService {

    private final LawyerMapper lawyerMapper;
    private final ClientMapper clientMapper;
    private final ProjectMapper projectMapper;
    private final ProjectService projectService;
    private final IncomeService incomeService;

    private final DataFormatter dataFormatter = new DataFormatter();

    @Transactional
    public Map<String, ImportResult> importWorkbook(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Map<String, ImportResult> result = new HashMap<>();
            result.put("lawyer", importLawyers(workbook.getSheet("律师")));
            result.put("client", importClients(workbook.getSheet("委托人")));
            result.put("project", importProjects(workbook.getSheet("项目")));
            result.put("income", importIncomes(workbook.getSheet("项目收入")));
            return result;
        } catch (IOException e) {
            throw new BusinessException("读取Excel失败：" + e.getMessage());
        }
    }

    private ImportResult importLawyers(Sheet sheet) {
        ImportResult result = new ImportResult();
        if (sheet == null) {
            result.getErrors().add("未找到【律师】工作表");
            return result;
        }
        int rowCount = sheet.getLastRowNum();
        for (int i = 1; i <= rowCount; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            result.setTotalRows(result.getTotalRows() + 1);
            try {
                String name = getStringValue(row.getCell(0));
                if (name == null || name.isBlank()) {
                    result.addError(i + 1, "姓名不能为空");
                    result.setFailRows(result.getFailRows() + 1);
                    continue;
                }
                Lawyer exists = lawyerMapper.selectOne(
                        new QueryWrapper<Lawyer>().eq("name", name).eq("deleted", 0));
                if (exists != null) {
                    result.addError(i + 1, "律师【" + name + "】已存在");
                    result.setFailRows(result.getFailRows() + 1);
                    continue;
                }
                Lawyer lawyer = new Lawyer();
                lawyer.setName(name);
                lawyer.setEmployeeNo(getStringValue(row.getCell(1)));
                lawyer.setPhone(getStringValue(row.getCell(2)));
                lawyer.setIdCard(getStringValue(row.getCell(3)));
                lawyer.setEntryDate(parseDate(row.getCell(4)));
                lawyer.setCommissionRate(parseDecimal(row.getCell(5), new BigDecimal("0.85")));
                lawyer.setOpeningBalance(parseDecimal(row.getCell(6), BigDecimal.ZERO));
                lawyer.setStatus(1);
                lawyerMapper.insert(lawyer);
                result.setSuccessRows(result.getSuccessRows() + 1);
            } catch (Exception e) {
                result.addError(i + 1, e.getMessage());
                result.setFailRows(result.getFailRows() + 1);
            }
        }
        return result;
    }

    private ImportResult importClients(Sheet sheet) {
        ImportResult result = new ImportResult();
        if (sheet == null) {
            result.getErrors().add("未找到【委托人】工作表");
            return result;
        }
        int rowCount = sheet.getLastRowNum();
        for (int i = 1; i <= rowCount; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            result.setTotalRows(result.getTotalRows() + 1);
            try {
                String name = getStringValue(row.getCell(0));
                if (name == null || name.isBlank()) {
                    result.addError(i + 1, "姓名不能为空");
                    result.setFailRows(result.getFailRows() + 1);
                    continue;
                }
                Client exists = clientMapper.selectOne(
                        new QueryWrapper<Client>().eq("name", name).eq("deleted", 0));
                if (exists != null) {
                    result.addError(i + 1, "【" + name + "】已存在");
                    result.setFailRows(result.getFailRows() + 1);
                    continue;
                }
                Client client = new Client();
                client.setName(name);
                client.setClientType(parseInt(row.getCell(1), 1));
                client.setPhone(getStringValue(row.getCell(2)));
                client.setIdCard(getStringValue(row.getCell(3)));
                client.setAddress(getStringValue(row.getCell(4)));
                client.setRemark(getStringValue(row.getCell(5)));
                clientMapper.insert(client);
                result.setSuccessRows(result.getSuccessRows() + 1);
            } catch (Exception e) {
                result.addError(i + 1, e.getMessage());
                result.setFailRows(result.getFailRows() + 1);
            }
        }
        return result;
    }

    private ImportResult importProjects(Sheet sheet) {
        ImportResult result = new ImportResult();
        if (sheet == null) {
            result.getErrors().add("未找到【项目】工作表");
            return result;
        }
        Map<String, Long> clientMap = loadClientMap();
        Map<String, Long> lawyerMap = loadLawyerMap();

        int rowCount = sheet.getLastRowNum();
        for (int i = 1; i <= rowCount; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            result.setTotalRows(result.getTotalRows() + 1);
            try {
                String projectNo = getStringValue(row.getCell(0));
                if (projectNo == null || projectNo.isBlank()) {
                    result.addError(i + 1, "项目编号不能为空");
                    result.setFailRows(result.getFailRows() + 1);
                    continue;
                }
                Project exists = projectMapper.selectOne(
                        new QueryWrapper<Project>().eq("project_no", projectNo).eq("deleted", 0));
                if (exists != null) {
                    result.addError(i + 1, "项目编号【" + projectNo + "】已存在");
                    result.setFailRows(result.getFailRows() + 1);
                    continue;
                }
                String projectName = getStringValue(row.getCell(1));
                if (projectName == null || projectName.isBlank()) {
                    result.addError(i + 1, "项目名称不能为空");
                    result.setFailRows(result.getFailRows() + 1);
                    continue;
                }
                Long clientId = clientMap.get(getStringValue(row.getCell(2)));
                Long partyId = clientMap.get(getStringValue(row.getCell(3)));
                Long hostLawyerId = lawyerMap.get(getStringValue(row.getCell(7)));
                if (clientId == null) {
                    result.addError(i + 1, "委托人不存在");
                    result.setFailRows(result.getFailRows() + 1);
                    continue;
                }
                if (hostLawyerId == null) {
                    result.addError(i + 1, "主办律师不存在");
                    result.setFailRows(result.getFailRows() + 1);
                    continue;
                }

                ProjectRequest request = new ProjectRequest();
                request.setProjectNo(projectNo);
                request.setProjectName(projectName);
                request.setClientId(clientId);
                request.setPartyId(partyId);
                request.setCaseCause(getStringValue(row.getCell(4)));
                request.setPartyIdentity(getStringValue(row.getCell(5)));
                request.setBusinessType(getStringValue(row.getCell(6)));
                request.setHostLawyerId(hostLawyerId);
                request.setAssistLawyerId(lawyerMap.get(getStringValue(row.getCell(8))));
                request.setCaseDate(parseDate(row.getCell(9), LocalDate.now()));
                request.setContractAmount(parseDecimal(row.getCell(10), BigDecimal.ZERO));
                request.setArchiveFee(parseDecimal(row.getCell(11), BigDecimal.ZERO));
                request.setRemark(getStringValue(row.getCell(12)));
                projectService.createProject(request);
                result.setSuccessRows(result.getSuccessRows() + 1);
            } catch (Exception e) {
                result.addError(i + 1, e.getMessage());
                result.setFailRows(result.getFailRows() + 1);
            }
        }
        return result;
    }

    private ImportResult importIncomes(Sheet sheet) {
        ImportResult result = new ImportResult();
        if (sheet == null) {
            result.getErrors().add("未找到【项目收入】工作表");
            return result;
        }
        int rowCount = sheet.getLastRowNum();
        for (int i = 1; i <= rowCount; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            result.setTotalRows(result.getTotalRows() + 1);
            try {
                String projectNo = getStringValue(row.getCell(0));
                if (projectNo == null || projectNo.isBlank()) {
                    result.addError(i + 1, "项目编号不能为空");
                    result.setFailRows(result.getFailRows() + 1);
                    continue;
                }
                Project project = projectMapper.selectOne(
                        new QueryWrapper<Project>().eq("project_no", projectNo).eq("deleted", 0));
                if (project == null) {
                    result.addError(i + 1, "项目编号【" + projectNo + "】不存在");
                    result.setFailRows(result.getFailRows() + 1);
                    continue;
                }
                LocalDate incomeDate = parseDate(row.getCell(1));
                if (incomeDate == null) {
                    result.addError(i + 1, "收入日期不能为空或格式错误");
                    result.setFailRows(result.getFailRows() + 1);
                    continue;
                }
                BigDecimal incomeAmount = parseDecimal(row.getCell(2), null);
                if (incomeAmount == null || incomeAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    result.addError(i + 1, "收入总额必须大于0");
                    result.setFailRows(result.getFailRows() + 1);
                    continue;
                }
                ProjectIncomeRequest request = new ProjectIncomeRequest();
                request.setProjectId(project.getId());
                request.setIncomeDate(incomeDate);
                request.setIncomeAmount(incomeAmount);
                request.setHostAmount(parseDecimal(row.getCell(3), BigDecimal.ZERO));
                request.setAssistAmount(parseDecimal(row.getCell(4), BigDecimal.ZERO));
                request.setRemark(getStringValue(row.getCell(5)));
                incomeService.createIncome(request);
                result.setSuccessRows(result.getSuccessRows() + 1);
            } catch (Exception e) {
                result.addError(i + 1, e.getMessage());
                result.setFailRows(result.getFailRows() + 1);
            }
        }
        return result;
    }

    private Map<String, Long> loadClientMap() {
        List<Client> clients = clientMapper.selectList(new QueryWrapper<Client>().eq("deleted", 0));
        Map<String, Long> map = new HashMap<>();
        for (Client client : clients) {
            map.put(client.getName(), client.getId());
        }
        return map;
    }

    private Map<String, Long> loadLawyerMap() {
        List<Lawyer> lawyers = lawyerMapper.selectList(new QueryWrapper<Lawyer>().eq("deleted", 0));
        Map<String, Long> map = new HashMap<>();
        for (Lawyer lawyer : lawyers) {
            map.put(lawyer.getName(), lawyer.getId());
        }
        return map;
    }

    private String getStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return null;
            }
            return dataFormatter.formatCellValue(cell).trim();
        }
        return dataFormatter.formatCellValue(cell).trim();
    }

    private LocalDate parseDate(Cell cell) {
        return parseDate(cell, null);
    }

    private LocalDate parseDate(Cell cell, LocalDate defaultValue) {
        if (cell == null) {
            return defaultValue;
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            Date date = cell.getDateCellValue();
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        String value = getStringValue(cell);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ofPattern("yyyyMMdd")
        );
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(value, formatter);
            } catch (Exception ignored) {
            }
        }
        return defaultValue;
    }

    private BigDecimal parseDecimal(Cell cell, BigDecimal defaultValue) {
        if (cell == null) {
            return defaultValue;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue()).setScale(2, RoundingMode.HALF_UP);
        }
        String value = getStringValue(cell);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private Integer parseInt(Cell cell, Integer defaultValue) {
        if (cell == null) {
            return defaultValue;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        String value = getStringValue(cell);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
