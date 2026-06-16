package com.farmtrace.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CooperativeResponse {
    private UUID id;
    private String name;
    private String region;
    private LocalDateTime createdAt;
}
