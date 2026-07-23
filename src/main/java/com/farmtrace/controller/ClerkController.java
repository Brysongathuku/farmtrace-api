package com.farmtrace.controller;

import com.farmtrace.dto.response.ApiResponse;
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
@RequestMapping("/api/clerk")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLERK')")
public class ClerkController {

    private final FarmerManagementService farmerManagementService;

    @GetMapping("/farmers/pending")
    public ResponseEntity<List<FarmerResponse>> getPendingFarmers(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(farmerManagementService.getPendingFarmers(currentUser));
    }

    @GetMapping("/farmers")
    public ResponseEntity<List<FarmerResponse>> getAllFarmers(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(farmerManagementService.getAllFarmers(currentUser));
    }

    @PatchMapping("/farmers/{id}/approve")
    public ResponseEntity<ApiResponse> approveFarmer(@PathVariable UUID id, @AuthenticationPrincipal User currentUser) {
        farmerManagementService.approveFarmer(id, currentUser);
        return ResponseEntity.ok(new ApiResponse("Farmer approved."));
    }

    @PatchMapping("/farmers/{id}/reject")
    public ResponseEntity<ApiResponse> rejectFarmer(
            @PathVariable UUID id,
            @RequestParam String reason,
            @AuthenticationPrincipal User currentUser) {
        farmerManagementService.rejectFarmer(id, currentUser, reason);
        return ResponseEntity.ok(new ApiResponse("Farmer rejected."));
    }
}