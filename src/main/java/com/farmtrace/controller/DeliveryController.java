package com.farmtrace.controller;

import com.farmtrace.entity.Delivery;
import com.farmtrace.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deliveries")
@CrossOrigin(origins = "*")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    /**
     * Record a new delivery
     * POST /api/deliveries
     * Called by: Clerk
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> recordDelivery(
            @RequestBody Delivery delivery) {
        try {
            Delivery saved = deliveryService.recordDelivery(delivery);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Delivery recorded successfully");
            response.put("data", saved);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get all deliveries
     * GET /api/deliveries
     * Called by: Clerk, Manager
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDeliveries() {
        try {
            List<Delivery> deliveries = deliveryService.getAllDeliveries();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Deliveries retrieved successfully");
            response.put("data", deliveries);
            response.put("count", deliveries.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get a specific delivery by ID
     * GET /api/deliveries/{id}
     * Called by: Clerk, Manager
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDeliveryById(
            @PathVariable Long id) {
        try {
            Delivery delivery = deliveryService.getDeliveryById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Delivery retrieved successfully");
            response.put("data", delivery);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get all deliveries for a specific farmer
     * GET /api/farmers/{farmerId}/deliveries
     * Called by: Farmer
     */
    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<Map<String, Object>> getFarmerDeliveries(
            @PathVariable Long farmerId) {
        try {
            Map<String, Object> summary =
                    deliveryService.getFarmerDeliverySummary(farmerId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Farmer deliveries retrieved successfully");
            response.put("data", summary);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get delivery by receipt number
     * GET /api/deliveries/receipt/{receiptNumber}
     * Called by: Farmer, Clerk
     */
    @GetMapping("/receipt/{receiptNumber}")
    public ResponseEntity<Map<String, Object>> getDeliveryByReceipt(
            @PathVariable String receiptNumber) {
        try {
            Delivery delivery =
                    deliveryService.getDeliveryByReceiptNumber(receiptNumber);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Receipt retrieved successfully");
            response.put("data", delivery);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Void a delivery
     * PUT /api/deliveries/{id}/void
     * Called by: Manager
     */
    @PutMapping("/{id}/void")
    public ResponseEntity<Map<String, Object>> voidDelivery(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");
            Delivery delivery = deliveryService.voidDelivery(id, reason);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Delivery voided successfully");
            response.put("data", delivery);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}