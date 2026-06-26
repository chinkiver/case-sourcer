package com.lawfirm.caseledger.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImportResult {

    private int totalRows;
    private int successRows;
    private int failRows;
    private List<String> errors = new ArrayList<>();

    public void addError(int row, String message) {
        errors.add("第" + row + "行：" + message);
    }
}
