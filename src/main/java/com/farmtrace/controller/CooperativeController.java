package com.farmtrace.controller;

import com.farmtrace.dto.request.CreateCooperativeRequest;
import com.farmtrace.dto.response.ApiResponse;
import com.farmtrace.dto.response.CooperativeResponse;
import com.farmtrace.service.CooperativeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cooperatives")
@RequiredArgsConstructor
public class CooperativeController {

    private final CooperativeService cooperativeService;

    @GetMapping
    public ResponseEntity<List<CooperativeResponse>> getAllCooperatives() {
        return ResponseEntity.ok(cooperativeService.getAllCooperatives());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createCooperative(@Valid @RequestBody CreateCooperativeRequest request) {
        cooperativeService.createCooperative(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Cooperative created."));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteCooperative(@PathVariable UUID id) {
        cooperativeService.deleteCooperative(id);
        return ResponseEntity.ok(new ApiResponse("Cooperative deleted."));
    }
}
