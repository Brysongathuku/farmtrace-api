package com.farmtrace.dto.response;

import com.farmtrace.enums.BatchStatus;
import com.farmtrace.enums.Grade;
import com.farmtrace.model.Batch;
import com.farmtrace.model.BatchAllocation;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
public class BatchResponse {
    private UUID id;
    private String batchNumber;

    private UUID collectionCenterId;
    private String collectionCenterName;

    private Grade grade;
    private BigDecimal capacityKg;
    private BigDecimal currentWeightKg;
    private BatchStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime dispatchedAt;

    // Populated only on the detail endpoint (traceability view) — the list
    // endpoint omits this to avoid pulling every allocation for every batch.
    private List<BatchAllocationResponse> contributingFarmers;

    public static BatchResponse from(Batch b) {
        return from(b, null);
    }

    public static BatchResponse from(Batch b, List<BatchAllocation> allocations) {
        BatchResponse.BatchResponseBuilder builder = BatchResponse.builder()
                .id(b.getId())
                .batchNumber(b.getBatchNumber())
                .collectionCenterId(b.getCollectionCenter().getId())
                .collectionCenterName(b.getCollectionCenter().getName())
                .grade(b.getGrade())
                .capacityKg(b.getCapacityKg())
                .currentWeightKg(b.getCurrentWeightKg())
                .status(b.getStatus())
                .createdAt(b.getCreatedAt())
                .dispatchedAt(b.getDispatchedAt());

        if (allocations != null) {
            builder.contributingFarmers(allocations.stream()
                    .map(a -> BatchAllocationResponse.builder()
                            .deliveryId(a.getDelivery().getId())
                            .receiptNumber(a.getDelivery().getReceiptNumber())
                            .farmerId(a.getDelivery().getFarmer().getId())
                            .farmerName(a.getDelivery().getFarmer().getFullName())
                            .farmerNationalId(a.getDelivery().getFarmer().getNationalId())
                            .allocatedKg(a.getAllocatedKg())
                            .build())
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }
}
