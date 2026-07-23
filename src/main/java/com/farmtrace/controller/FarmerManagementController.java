package com.farmtrace.controller;

import com.farmtrace.dto.response.FarmerResponse;
import com.farmtrace.model.User;
import com.farmtrace.service.FarmerManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/farmers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLERK')")
public class FarmerManagementController {

    private final FarmerManagementService farmerManagementService;

    // GET all farmers in the clerk's own cooperative
    @GetMapping
    public ResponseEntity<List<FarmerResponse>> getAllFarmers(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(farmerManagementService.getAllFarmers(currentUser));
    }

    // GET pending farmers in the clerk's own cooperative
    @GetMapping("/pending")
    public ResponseEntity<List<FarmerResponse>> getPendingFarmers(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(farmerManagementService.getPendingFarmers(currentUser));
    }

    // APPROVE farmer — must belong to the clerk's own cooperative
    @PutMapping("/{id}/approve")
    public ResponseEntity<FarmerResponse> approveFarmer(@PathVariable UUID id, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(farmerManagementService.approveFarmer(id, currentUser));
    }

    // REJECT farmer — must belong to the clerk's own cooperative
    @PutMapping("/{id}/reject")
    public ResponseEntity<FarmerResponse> rejectFarmer(
            @PathVariable UUID id,
            @RequestParam String reason,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(farmerManagementService.rejectFarmer(id, currentUser, reason));
    }
}