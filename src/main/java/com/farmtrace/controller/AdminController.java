package com.farmtrace.controller;

import com.farmtrace.dto.request.CreateClerkRequest;
import com.farmtrace.dto.request.CreateCollectionCenterRequest;
import com.farmtrace.dto.request.UpdateGradePriceRequest;
import com.farmtrace.dto.response.ApiResponse;
import com.farmtrace.dto.response.AuditLogResponse;
import com.farmtrace.dto.response.CollectionCenterResponse;
import com.farmtrace.dto.response.DashboardResponse;
import com.farmtrace.dto.response.FarmerResponse;
import com.farmtrace.dto.response.GradePriceResponse;
import com.farmtrace.dto.response.UserResponse;
import com.farmtrace.enums.FarmerStatus;
import com.farmtrace.service.AdminService;
import com.farmtrace.service.AuditLogService;
import com.farmtrace.service.CollectionCenterService;
import com.farmtrace.service.FarmerManagementService;
import com.farmtrace.service.GradePriceService;
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
    private final AuditLogService auditLogService;
    private final CollectionCenterService collectionCenterService;
    private final GradePriceService gradePriceService;

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

    @GetMapping("/farmers")
    public ResponseEntity<List<FarmerResponse>> getAllFarmers(
            @RequestParam(required = false) FarmerStatus status,
            @RequestParam(required = false) UUID cooperativeId) {
        return ResponseEntity.ok(
                farmerManagementService.getAllFarmersForAdmin(status, cooperativeId)
        );
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/audit-log")
    public ResponseEntity<List<AuditLogResponse>> getAuditLog(
            @RequestParam(required = false) String action) {
        if (action != null && !action.isBlank()) {
            return ResponseEntity.ok(auditLogService.getLogsByAction(action));
        }
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    @PostMapping("/collection-centers")
    public ResponseEntity<CollectionCenterResponse> createCollectionCenter(
            @Valid @RequestBody CreateCollectionCenterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(collectionCenterService.createCollectionCenter(request));
    }

    @GetMapping("/collection-centers")
    public ResponseEntity<List<CollectionCenterResponse>> getCollectionCenters(
            @RequestParam(required = false) UUID cooperativeId) {
        if (cooperativeId != null) {
            return ResponseEntity.ok(collectionCenterService.getCollectionCentersByCooperative(cooperativeId));
        }
        return ResponseEntity.ok(collectionCenterService.getAllCollectionCenters());
    }

    @DeleteMapping("/collection-centers/{id}")
    public ResponseEntity<ApiResponse> deleteCollectionCenter(@PathVariable UUID id) {
        collectionCenterService.deleteCollectionCenter(id);
        return ResponseEntity.ok(new ApiResponse("Collection center deleted."));
    }

    @PutMapping("/grade-prices")
    public ResponseEntity<GradePriceResponse> updateGradePrice(
            @Valid @RequestBody UpdateGradePriceRequest request) {
        return ResponseEntity.ok(gradePriceService.updatePrice(request));
    }

    @GetMapping("/grade-prices")
    public ResponseEntity<List<GradePriceResponse>> getCurrentPrices() {
        return ResponseEntity.ok(gradePriceService.getCurrentPrices());
    }

    @GetMapping("/grade-prices/history")
    public ResponseEntity<List<GradePriceResponse>> getPriceHistory() {
        return ResponseEntity.ok(gradePriceService.getPriceHistory());
    }
}