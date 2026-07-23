package com.farmtrace.controller;

import com.farmtrace.dto.request.CreateClerkRequest;
import com.farmtrace.dto.response.ApiResponse;
import com.farmtrace.dto.response.FarmerResponse;
import com.farmtrace.dto.response.UserResponse;
import com.farmtrace.enums.FarmerStatus;
import com.farmtrace.service.AdminService;
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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final FarmerManagementService farmerManagementService;

    @PostMapping("/clerks")
    public ResponseEntity<ApiResponse> createClerk(@Valid @RequestBody CreateClerkRequest request) {
        adminService.createClerk(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Clerk account created."));
    }

    @GetMapping("/clerks")
    public ResponseEntity<List<UserResponse>> getClerks() {
        return ResponseEntity.ok(adminService.getAllClerks());
    }

    @DeleteMapping("/clerks/{id}")
    public ResponseEntity<ApiResponse> deleteClerk(@PathVariable UUID id) {
        adminService.deleteClerk(id);
        return ResponseEntity.ok(new ApiResponse("Clerk removed."));
    }

    // GET all farmers across all cooperatives (admin view-only), with
    // optional status and/or cooperativeId filters.
    @GetMapping("/farmers")
    public ResponseEntity<List<FarmerResponse>> getAllFarmers(
            @RequestParam(required = false) FarmerStatus status,
            @RequestParam(required = false) UUID cooperativeId) {
        return ResponseEntity.ok(
                farmerManagementService.getAllFarmersForAdmin(status, cooperativeId)
        );
    }
}