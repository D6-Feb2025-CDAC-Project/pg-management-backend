package com.easypg.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepositSettlementDTO {
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal deductionAmount;
    private String deductionReason;
}
