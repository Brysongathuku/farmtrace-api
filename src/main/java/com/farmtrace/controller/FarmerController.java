package com.farmtrace.controller;

import com.farmtrace.entity.Farmer;
import com.farmtrace.service.FarmerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/farmers")
@CrossOrigin(origins = "*")
public class FarmerController {

    @Autowired
    private FarmerService farmerService;

    /**
     * Farmer self-registration
     * POST /api/farmers/register
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerFarmer(@RequestBody Farmer farmer) {
        try {
            Farmer registeredFarmer = farmerService.registerFarmer(farmer);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration submitted successfully. A field officer will contact you within 48 hours.");
            
            Map<String, Object> data = new HashMap<>();
            data.put("id", registeredFarmer.getId());
            data.put("name", registeredFarmer.getName());
            data.put("nationalId", registeredFarmer.getNationalId());
            data.put("phone", registeredFarmer.getPhone());
            data.put("farmSize", registeredFarmer.getFarmSize());
            data.put("farmSizeCategory", registeredFarmer.getFarmSizeCategory());
            data.put("colorCode", registeredFarmer.getColorCode());
            data.put("status", registeredFarmer.getStatus());
            data.put("registeredAt", registeredFarmer.getRegisteredAt());
            
            response.put("data", data);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Get farmer status
     * GET /api/farmers/{id}/status
     */
    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> getFarmerStatus(@PathVariable Long id) {
        try {
            Map<String, Object> status = farmerService.getFarmerStatus(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Status retrieved successfully");
            response.put("data", status);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Get farmer profile
     * GET /api/farmers/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getFarmer(@PathVariable Long id) {
        try {
            Farmer farmer = farmerService.getFarmerById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Farmer retrieved successfully");
            
            Map<String, Object> data = new HashMap<>();
            data.put("id", farmer.getId());
            data.put("name", farmer.getName());
            data.put("nationalId", farmer.getNationalId());
            data.put("phone", farmer.getPhone());
            data.put("farmSize", farmer.getFarmSize());
            data.put("farmSizeCategory", farmer.getFarmSizeCategory());
            data.put("colorCode", farmer.getColorCode());
            data.put("primaryCrop", farmer.getPrimaryCrop());
            data.put("status", farmer.getStatus());
            data.put("registeredAt", farmer.getRegisteredAt());
            data.put("approvedAt", farmer.getApprovedAt());
            data.put("farmLatitude", farmer.getFarmLatitude());
            data.put("farmLongitude", farmer.getFarmLongitude());
            
            response.put("data", data);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}
