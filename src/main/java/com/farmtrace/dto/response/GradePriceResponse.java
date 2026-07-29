package com.farmtrace.dto.response;

import com.farmtrace.enums.Grade;
import com.farmtrace.model.GradePrice;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class GradePriceResponse {
    private UUID id;
    private Grade grade;
    private BigDecimal pricePerKg;
    private String updatedByEmail;
    private LocalDateTime effectiveFrom;

    public static GradePriceResponse from(GradePrice price) {
        return GradePriceResponse.builder()
                .id(price.getId())
                .grade(price.getGrade())
                .pricePerKg(price.getPricePerKg())
                .updatedByEmail(price.getUpdatedByEmail())
                .effectiveFrom(price.getEffectiveFrom())
                .build();
    }
}
