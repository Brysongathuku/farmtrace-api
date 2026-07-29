package com.farmtrace.dto.request;

import com.farmtrace.enums.Grade;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateGradePriceRequest {

    @NotNull(message = "Grade is required")
    private Grade grade;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal pricePerKg;
}
