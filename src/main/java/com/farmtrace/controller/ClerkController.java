package com.farmtrace.controller;

import com.farmtrace.dto.response.ApiResponse;
import com.farmtrace.dto.response.FarmerResponse;
import com.farmtrace.service.ClerkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clerk")
@RequiredArgsConstructor
public class ClerkController {

    private final ClerkService clerkService;

    @GetMapping("/farmers/pending")
    @PreAuthorize("hasRole('CLERK')")
    public ResponseEntity<List<FarmerResponse>> getPendingFarmers() {
        return ResponseEntity.ok(clerkService.getPendingFarmers());
    }

    @GetMapping("/farmers")
    @PreAuthorize("hasRole('CLERK')")
    public ResponseEntity<List<FarmerResponse>> getAllFarmers() {
        return ResponseEntity.ok(clerkService.getAllFarmers());
    }

    @PatchMapping("/farmers/{id}/approve")
    @PreAuthorize("hasRole('CLERK')")
    public ResponseEntity<ApiResponse> approveFarmer(@PathVariable UUID id) {
        clerkService.approveFarmer(id);
        return ResponseEntity.ok(new ApiResponse("Farmer approved."));
    }

    @PatchMapping("/farmers/{id}/reject")
    @PreAuthorize("hasRole('CLERK')")
    public ResponseEntity<ApiResponse> rejectFarmer(@PathVariable UUID id, @RequestParam String reason) {
        clerkService.rejectFarmer(id, reason);
        return ResponseEntity.ok(new ApiResponse("Farmer rejected."));
    }
}
