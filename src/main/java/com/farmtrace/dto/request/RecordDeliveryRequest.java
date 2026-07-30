package com.farmtrace.dto.request;

import com.farmtrace.enums.DeliveryStatus;
import com.farmtrace.enums.Grade;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class RecordDeliveryRequest {

    @NotNull(message = "Farmer is required")
    private UUID farmerId;

    @NotNull(message = "Collection center is required")
    private UUID collectionCenterId;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than zero")
    private BigDecimal quantityKg;

    @NotNull(message = "Grade is required")
    private Grade grade;

    @NotNull(message = "Moisture content is required")
    @DecimalMin(value = "0.0", message = "Moisture content cannot be negative")
    private BigDecimal moistureContent;

    @NotNull(message = "Status is required")
    private DeliveryStatus status;

    // Required only when status is REJECTED — validated in the service layer,
    // since @NotNull here would wrongly require it for approved deliveries too.
    private String rejectionReason;
}