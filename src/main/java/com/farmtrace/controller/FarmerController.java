package com.farmtrace.controller;

import com.farmtrace.dto.response.ApiResponse;
import com.farmtrace.dto.response.FarmerResponse;
import com.farmtrace.service.FarmerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/farmer")
@RequiredArgsConstructor
public class FarmerController {

    private final FarmerService farmerService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<FarmerResponse> getProfile(@AuthenticationPrincipal UUID farmerId) {
        return ResponseEntity.ok(farmerService.getFarmerProfile(farmerId));
    }

    @PatchMapping("/notification/acknowledge")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<ApiResponse> acknowledgeNotification(@AuthenticationPrincipal UUID farmerId) {
        farmerService.acknowledgeNotification(farmerId);
        return ResponseEntity.ok(new ApiResponse("Notification acknowledged."));
    }
}
