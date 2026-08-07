package com.farmtrace.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class BatchAllocationResponse {
    private UUID deliveryId;
    private String receiptNumber;
    private UUID farmerId;
    private String farmerName;
    private String farmerNationalId;
    private BigDecimal allocatedKg;
}
