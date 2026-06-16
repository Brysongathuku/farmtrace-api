package com.farmtrace.controller;

import com.farmtrace.dto.response.FarmerResponse;
import com.farmtrace.service.FarmerManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/farmers")
@RequiredArgsConstructor
public class FarmerManagementController {

    private final FarmerManagementService farmerManagementService;

    // GET all farmers — CLERK and ADMIN only
    @GetMapping
    @PreAuthorize("hasAnyRole('CLERK', 'ADMIN')")
    public ResponseEntity<List<FarmerResponse>> getAllFarmers() {
        return ResponseEntity.ok(farmerManagementService.getAllFarmers());
    }

    // GET pending farmers — CLERK and ADMIN only
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('CLERK', 'ADMIN')")
    public ResponseEntity<List<FarmerResponse>> getPendingFarmers() {
        return ResponseEntity.ok(farmerManagementService.getPendingFarmers());
    }

    // APPROVE farmer — CLERK and ADMIN only
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('CLERK', 'ADMIN')")
    public ResponseEntity<FarmerResponse> approveFarmer(@PathVariable UUID id) {
        return ResponseEntity.ok(farmerManagementService.approveFarmer(id));
    }

    // REJECT farmer — CLERK and ADMIN only
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('CLERK', 'ADMIN')")
    public ResponseEntity<FarmerResponse> rejectFarmer(@PathVariable UUID id) {
        return ResponseEntity.ok(farmerManagementService.rejectFarmer(id));
    }
}
