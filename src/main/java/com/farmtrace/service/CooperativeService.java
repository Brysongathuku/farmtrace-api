package com.farmtrace.service;

import com.farmtrace.dto.request.CreateCooperativeRequest;
import com.farmtrace.dto.response.CooperativeResponse;
import com.farmtrace.exception.ConflictException;
import com.farmtrace.exception.ResourceNotFoundException;
import com.farmtrace.model.Cooperative;
import com.farmtrace.repository.CooperativeRepository;
import lombok.RequiredArgsConstructor;
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

    public CooperativeResponse createCooperative(CreateCooperativeRequest request) {
        if (cooperativeRepository.existsByName(request.getName())) {
            throw new ConflictException("Cooperative with this name already exists");
        }

        Cooperative cooperative = Cooperative.builder()
                .name(request.getName())
                .region(request.getRegion())
                .build();

        Cooperative saved = cooperativeRepository.save(cooperative);
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
        if (!cooperativeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cooperative not found");
        }
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
}
