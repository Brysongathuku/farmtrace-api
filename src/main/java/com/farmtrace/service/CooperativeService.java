package com.farmtrace.service;

import com.farmtrace.dto.request.CreateCooperativeRequest;
import com.farmtrace.dto.response.CooperativeResponse;
import com.farmtrace.exception.ConflictException;
import com.farmtrace.exception.ResourceNotFoundException;
import com.farmtrace.model.Cooperative;
import com.farmtrace.model.User;
import com.farmtrace.repository.CooperativeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CooperativeService {

    private final CooperativeRepository cooperativeRepository;
    private final AuditLogService auditLogService;

    public CooperativeResponse createCooperative(CreateCooperativeRequest request) {
        if (cooperativeRepository.existsByName(request.getName())) {
            throw new ConflictException("Cooperative with this name already exists");
        }

        Cooperative cooperative = Cooperative.builder()
                .name(request.getName())
                .region(request.getRegion())
                .build();

        Cooperative saved = cooperativeRepository.save(cooperative);

        auditLogService.log(
                "CREATE_COOPERATIVE",
                getCurrentAdminEmail(),
                "COOPERATIVE",
                "Created cooperative " + saved.getName() + " (" + saved.getRegion() + ")"
        );

        return toResponse(saved);
    }

    public List<CooperativeResponse> getAllCooperatives() {
        return cooperativeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CooperativeResponse getCooperativeById(UUID id) {
        Cooperative cooperative = cooperativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cooperative not found"));
        return toResponse(cooperative);
    }

    public void deleteCooperative(UUID id) {
        Cooperative cooperative = cooperativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cooperative not found"));

        auditLogService.log(
                "DELETE_COOPERATIVE",
                getCurrentAdminEmail(),
                "COOPERATIVE",
                "Deleted cooperative " + cooperative.getName() + " (" + cooperative.getRegion() + ")"
        );

        cooperativeRepository.deleteById(id);
    }

    private CooperativeResponse toResponse(Cooperative cooperative) {
        return CooperativeResponse.builder()
                .id(cooperative.getId())
                .name(cooperative.getName())
                .region(cooperative.getRegion())
                .createdAt(cooperative.getCreatedAt())
                .build();
    }

    private String getCurrentAdminEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            return user.getEmail();
        }
        return "unknown";
    }
}