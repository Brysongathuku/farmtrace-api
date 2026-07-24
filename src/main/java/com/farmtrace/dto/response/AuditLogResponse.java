package com.farmtrace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    private UUID id;
    private String action;
    private String actionType;
    private String performedBy;
    private String targetEntity;
    private String details;
    private LocalDateTime timestamp;

    public static AuditLogResponse from(com.farmtrace.model.AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .action(log.getAction())
                .actionType(log.getActionType())
                .performedBy(log.getPerformedBy())
                .targetEntity(log.getTargetEntity())
                .details(log.getDetails())
                .timestamp(log.getTimestamp())
                .build();
    }
}
