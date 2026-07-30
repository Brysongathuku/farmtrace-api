package com.farmtrace.dto.response;

import com.farmtrace.enums.DeliveryStatus;
import com.farmtrace.enums.Grade;
import com.farmtrace.model.Delivery;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DeliveryResponse {
    private UUID id;
    private String receiptNumber;

    private UUID farmerId;
    private String farmerName;
    private String farmerNationalId;

    private UUID collectionCenterId;
    private String collectionCenterName;

    private String recordedByClerkName;
    private String recordedByClerkEmail;

    private BigDecimal quantityKg;
    private Grade grade;
    private BigDecimal moistureContent;
    private BigDecimal pricePerKgAtDelivery;
    private BigDecimal totalValue;

    private DeliveryStatus status;
    private String rejectionReason;

    private LocalDateTime deliveryTimestamp;

    public static DeliveryResponse from(Delivery d) {
        return DeliveryResponse.builder()
                .id(d.getId())
                .receiptNumber(d.getReceiptNumber())
                .farmerId(d.getFarmer().getId())
                .farmerName(d.getFarmer().getFullName())
                .farmerNationalId(d.getFarmer().getNationalId())
                .collectionCenterId(d.getCollectionCenter().getId())
                .collectionCenterName(d.getCollectionCenter().getName())
                .recordedByClerkName(d.getRecordedByClerk().getFullName())
                .recordedByClerkEmail(d.getRecordedByClerk().getEmail())
                .quantityKg(d.getQuantityKg())
                .grade(d.getGrade())
                .moistureContent(d.getMoistureContent())
                .pricePerKgAtDelivery(d.getPricePerKgAtDelivery())
                .totalValue(d.getTotalValue())
                .status(d.getStatus())
                .rejectionReason(d.getRejectionReason())
                .deliveryTimestamp(d.getDeliveryTimestamp())
                .build();
    }
}