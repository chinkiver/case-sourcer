package com.lawfirm.caseledger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TaxBracket {

    private BigDecimal limit;
    private BigDecimal rate;
    private BigDecimal quickDeduction;
}
