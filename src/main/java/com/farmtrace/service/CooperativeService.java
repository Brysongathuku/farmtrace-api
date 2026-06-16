package com.farmtrace.service;

import com.farmtrace.dto.request.CreateCooperativeRequest;
import com.farmtrace.dto.response.CooperativeResponse;
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

    public List<CooperativeResponse> getAllCooperatives() {
        return cooperativeRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public void createCooperative(CreateCooperativeRequest request) {
        Cooperative cooperative = Cooperative.builder()
                .name(request.getName())
                .region(request.getRegion())
                .build();
        cooperativeRepository.save(cooperative);
    }

    public void deleteCooperative(UUID id) {
        cooperativeRepository.deleteById(id);
    }

    private CooperativeResponse convertToResponse(Cooperative cooperative) {
        return CooperativeResponse.builder()
                .id(cooperative.getId())
                .name(cooperative.getName())
                .region(cooperative.getRegion())
                .createdAt(cooperative.getCreatedAt())
                .build();
    }
}
