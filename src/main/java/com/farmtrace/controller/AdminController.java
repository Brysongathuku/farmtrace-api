package com.farmtrace.controller;

import com.farmtrace.dto.request.CreateClerkRequest;
import com.farmtrace.dto.response.ApiResponse;
import com.farmtrace.dto.response.UserResponse;
import com.farmtrace.service.AdminService;
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
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/clerks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createClerk(@Valid @RequestBody CreateClerkRequest request) {
        adminService.createClerk(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Clerk account created."));
    }

    @GetMapping("/clerks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getClerks() {
        return ResponseEntity.ok(adminService.getAllClerks());
    }

    @DeleteMapping("/clerks/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteClerk(@PathVariable UUID id) {
        adminService.deleteClerk(id);
        return ResponseEntity.ok(new ApiResponse("Clerk removed."));
    }
}
