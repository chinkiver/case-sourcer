package com.lawfirm.caseledger.controller;

import com.lawfirm.caseledger.common.Result;
import com.lawfirm.caseledger.dto.ImportResult;
import com.lawfirm.caseledger.service.ExcelImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ExcelImportController {

    private final ExcelImportService excelImportService;

    @PostMapping("/excel")
    @PreAuthorize("hasAuthority('import')")
    public Result<Map<String, ImportResult>> importExcel(@RequestParam("file") MultipartFile file) {
        return Result.success(excelImportService.importWorkbook(file));
    }
}
