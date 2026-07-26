package com.farmtrace.controller;

import com.farmtrace.dto.response.ApiResponse;
import com.farmtrace.dto.response.FarmerResponse;
import com.farmtrace.model.User;
import com.farmtrace.service.FarmerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/farmer")
@RequiredArgsConstructor
public class FarmerController {

    private final FarmerService farmerService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<FarmerResponse> getProfile(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(farmerService.getFarmerProfile(currentUser.getId()));
    }

    @PatchMapping("/notification/acknowledge")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<ApiResponse> acknowledgeNotification(@AuthenticationPrincipal User currentUser) {
        farmerService.acknowledgeNotification(currentUser.getId());
        return ResponseEntity.ok(new ApiResponse("Notification acknowledged."));
    }
}