package com.farmtrace.dto.response;

import com.farmtrace.model.BatchAllocation;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DeliveryBatchInfoResponse {
    private String batchNumber;
    private BigDecimal allocatedKg;

    public static DeliveryBatchInfoResponse from(BatchAllocation a) {
        return DeliveryBatchInfoResponse.builder()
                .batchNumber(a.getBatch().getBatchNumber())
                .allocatedKg(a.getAllocatedKg())
                .build();
    }
}

