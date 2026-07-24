package com.farmtrace.service;

import com.farmtrace.dto.response.AuditLogResponse;
import com.farmtrace.model.AuditLog;
import com.farmtrace.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(String action, String performedBy, String targetEntity, String details) {
        AuditLog entry = AuditLog.builder()
                .action(action)
                .actionType(deriveActionType(action))
                .performedBy(performedBy)
                .targetEntity(targetEntity)
                .details(details)
                .build();
        auditLogRepository.save(entry);
    }

    public List<AuditLogResponse> getAllLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc().stream()
                .map(AuditLogResponse::from)
                .collect(Collectors.toList());
    }

    public List<AuditLogResponse> getLogsByAction(String action) {
        return auditLogRepository.findByActionOrderByTimestampDesc(action).stream()
                .map(AuditLogResponse::from)
                .collect(Collectors.toList());
    }

    // Derives the general category from the specific action name —
    // e.g. CREATE_CLERK -> CREATE, REJECT_FARMER -> REJECT.
    private String deriveActionType(String action) {
        if (action.startsWith("CREATE")) return "CREATE";
        if (action.startsWith("DELETE")) return "DELETE";
        if (action.startsWith("APPROVE")) return "APPROVE";
        if (action.startsWith("REJECT")) return "REJECT";
        if (action.startsWith("ADMIN_LOGIN")) return "LOGIN";
        return "OTHER";
    }
}