package com.farmtrace.service;

import com.farmtrace.dto.request.UpdateGradePriceRequest;
import com.farmtrace.dto.response.GradePriceResponse;
import com.farmtrace.enums.Grade;
import com.farmtrace.exception.ResourceNotFoundException;
import com.farmtrace.model.GradePrice;
import com.farmtrace.model.User;
import com.farmtrace.repository.GradePriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GradePriceService {

    private final GradePriceRepository gradePriceRepository;
    private final AuditLogService auditLogService;

    // Inserts a new price row — never overwrites an existing one, so
    // full price history is preserved automatically.
    public GradePriceResponse updatePrice(UpdateGradePriceRequest request) {
        String adminEmail = getCurrentAdminEmail();

        GradePrice price = GradePrice.builder()
                .grade(request.getGrade())
                .pricePerKg(request.getPricePerKg())
                .updatedByEmail(adminEmail)
                .build();

        GradePrice saved = gradePriceRepository.save(price);

        auditLogService.log(
                "UPDATE_GRADE_PRICE",
                adminEmail,
                "GRADE_PRICE",
                "Set Grade " + request.getGrade() + " price to " + request.getPricePerKg() + " per kg"
        );

        return GradePriceResponse.from(saved);
    }

    // Current price = most recent row for each grade.
    public List<GradePriceResponse> getCurrentPrices() {
        return Arrays.stream(Grade.values())
                .map(grade -> gradePriceRepository.findCurrentPriceForGrade(grade)
                        .map(GradePriceResponse::from)
                        .orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Returns the current effective price for a grade — used internally by
    // the delivery-recording flow. Throws if no price has ever been set.
    public BigDecimal getCurrentPriceValue(Grade grade) {
        return gradePriceRepository.findCurrentPriceForGrade(grade)
                .map(GradePrice::getPricePerKg)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No price has been set for Grade " + grade + " yet"));
    }

    public List<GradePriceResponse> getPriceHistory() {
        return gradePriceRepository.findAllByOrderByEffectiveFromDesc().stream()
                .map(GradePriceResponse::from)
                .collect(Collectors.toList());
    }

    private String getCurrentAdminEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            return user.getEmail();
        }
        return "unknown";
    }
}
