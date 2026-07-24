package com.farmtrace.controller;

import com.farmtrace.dto.request.CreateCooperativeRequest;
import com.farmtrace.dto.response.ApiResponse;
import com.farmtrace.dto.response.CooperativeResponse;
import com.farmtrace.dto.response.FarmerResponse;
import com.farmtrace.dto.response.UserResponse;
import com.farmtrace.service.AdminService;
import com.farmtrace.service.CooperativeService;
import com.farmtrace.service.FarmerManagementService;
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
    private final FarmerManagementService farmerManagementService;
    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<List<CooperativeResponse>> getAllCooperatives() {
        return ResponseEntity.ok(cooperativeService.getAllCooperatives());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CooperativeResponse> getCooperativeById(@PathVariable UUID id) {
        return ResponseEntity.ok(cooperativeService.getCooperativeById(id));
    }

    @GetMapping("/{id}/farmers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FarmerResponse>> getFarmersByCooperative(@PathVariable UUID id) {
        return ResponseEntity.ok(farmerManagementService.getAllFarmersForAdmin(null, id));
    }

    @GetMapping("/{id}/clerks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getClerksByCooperative(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.getClerksByCooperative(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CooperativeResponse> createCooperative(@Valid @RequestBody CreateCooperativeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cooperativeService.createCooperative(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteCooperative(@PathVariable UUID id) {
        cooperativeService.deleteCooperative(id);
        return ResponseEntity.ok(new ApiResponse("Cooperative deleted."));
    }
}