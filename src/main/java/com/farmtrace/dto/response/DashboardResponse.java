package com.farmtrace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private long totalClerks;
    private long totalCooperatives;
    private long approvedFarmers;
    private long pendingFarmers;
    private long rejectedFarmers;
    private long unverifiedFarmers;
}