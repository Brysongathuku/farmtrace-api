package com.farmtrace.controller;

import com.farmtrace.dto.request.RecordDeliveryRequest;
import com.farmtrace.dto.response.ApiResponse;
import com.farmtrace.dto.response.BatchResponse;
import com.farmtrace.dto.response.CollectionCenterResponse;
import com.farmtrace.dto.response.DeliveryResponse;
import com.farmtrace.dto.response.FarmerResponse;
import com.farmtrace.dto.response.GradePriceResponse;
import com.farmtrace.model.Batch;
import com.farmtrace.model.BatchAllocation;
import com.farmtrace.model.User;
import com.farmtrace.service.BatchService;
import com.farmtrace.service.CollectionCenterService;
import com.farmtrace.service.DeliveryService;
import com.farmtrace.service.FarmerManagementService;
import com.farmtrace.service.GradePriceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clerk")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLERK')")
public class ClerkController {

    private final FarmerManagementService farmerManagementService;
    private final CollectionCenterService collectionCenterService;
    private final DeliveryService deliveryService;
    private final GradePriceService gradePriceService;
    private final BatchService batchService;

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

    @GetMapping("/collection-centers")
    public ResponseEntity<List<CollectionCenterResponse>> getMyCollectionCenters(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(collectionCenterService.getMyCollectionCenters(currentUser));
    }

    @PostMapping("/deliveries")
    public ResponseEntity<DeliveryResponse> recordDelivery(
            @Valid @RequestBody RecordDeliveryRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deliveryService.recordDelivery(request, currentUser));
    }

    @GetMapping("/deliveries")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByCollectionCenter(
            @RequestParam UUID collectionCenterId,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                deliveryService.getDeliveriesByCollectionCenter(collectionCenterId, currentUser));
    }

    @GetMapping("/grade-prices")
    public ResponseEntity<List<GradePriceResponse>> getCurrentPrices() {
        return ResponseEntity.ok(gradePriceService.getCurrentPrices());
    }

    // ── Batch endpoints ──────────────────────────────────────────────

    @GetMapping("/batches")
    public ResponseEntity<List<BatchResponse>> getBatchesForCenter(
            @RequestParam UUID collectionCenterId,
            @AuthenticationPrincipal User currentUser) {
        List<Batch> batches = batchService.getBatchesForCenter(collectionCenterId, currentUser);
        return ResponseEntity.ok(batches.stream().map(BatchResponse::from).collect(Collectors.toList()));
    }

    @GetMapping("/batches/{id}")
    public ResponseEntity<BatchResponse> getBatchDetail(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        Batch batch = batchService.getBatchOrThrow(id, currentUser);
        List<BatchAllocation> allocations = batchService.getAllocationsForBatch(id, currentUser);
        return ResponseEntity.ok(BatchResponse.from(batch, allocations));
    }

    @PatchMapping("/batches/{id}/dispatch")
    public ResponseEntity<BatchResponse> dispatchBatch(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        Batch batch = batchService.markDispatched(id, currentUser);
        return ResponseEntity.ok(BatchResponse.from(batch));
    }
}