package com.farmtrace.service;

import com.farmtrace.dto.request.CreateCollectionCenterRequest;
import com.farmtrace.dto.response.CollectionCenterResponse;
import com.farmtrace.exception.ResourceNotFoundException;
import com.farmtrace.model.CollectionCenter;
import com.farmtrace.model.Cooperative;
import com.farmtrace.model.User;
import com.farmtrace.repository.CollectionCenterRepository;
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
public class CollectionCenterService {

    private final CollectionCenterRepository collectionCenterRepository;
    private final CooperativeRepository cooperativeRepository;
    private final AuditLogService auditLogService;

    public CollectionCenterResponse createCollectionCenter(CreateCollectionCenterRequest request) {
        Cooperative cooperative = cooperativeRepository.findById(request.getCooperativeId())
                .orElseThrow(() -> new ResourceNotFoundException("Cooperative not found"));

        CollectionCenter center = CollectionCenter.builder()
                .name(request.getName())
                .location(request.getLocation())
                .cooperative(cooperative)
                .build();

        CollectionCenter saved = collectionCenterRepository.save(center);

        auditLogService.log(
                "CREATE_COLLECTION_CENTER",
                getCurrentAdminEmail(),
                "COLLECTION_CENTER",
                "Created collection center " + saved.getName() + " for " + cooperative.getName()
        );

        return CollectionCenterResponse.from(saved);
    }

    public List<CollectionCenterResponse> getAllCollectionCenters() {
        return collectionCenterRepository.findAll().stream()
                .map(CollectionCenterResponse::from)
                .collect(Collectors.toList());
    }

    public List<CollectionCenterResponse> getCollectionCentersByCooperative(UUID cooperativeId) {
        return collectionCenterRepository.findByCooperativeId(cooperativeId).stream()
                .map(CollectionCenterResponse::from)
                .collect(Collectors.toList());
    }

    public List<CollectionCenterResponse> getMyCollectionCenters(User currentUser) {
        if (currentUser.getCooperative() == null) {
            return List.of();
        }
        return getCollectionCentersByCooperative(currentUser.getCooperative().getId());
    }

    public void deleteCollectionCenter(UUID id) {
        CollectionCenter center = collectionCenterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection center not found"));

        auditLogService.log(
                "DELETE_COLLECTION_CENTER",
                getCurrentAdminEmail(),
                "COLLECTION_CENTER",
                "Deleted collection center " + center.getName()
        );

        collectionCenterRepository.deleteById(id);
    }

    private String getCurrentAdminEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            return user.getEmail();
        }
        return "unknown";
    }
}