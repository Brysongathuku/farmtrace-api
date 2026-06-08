package com.farmtrace.service;

import com.farmtrace.entity.Farmer;
import com.farmtrace.repository.FarmerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@SuppressWarnings("unused")
public class FarmerService {

    @Autowired
    private FarmerRepository farmerRepository;

    /**
     * Determine farm size category and color code based on acreage
     * 
     * LARGE (>10 acres)   -> Deep Green (#006400)
     * MEDIUM (5-10 acres) -> Light Green (#90EE90)
     * SMALL (<5 acres)    -> Yellow (#FFD700)
     */
    public Map<String, String> determineFarmSizeCategory(BigDecimal farmSize) {
        Map<String, String> result = new HashMap<>();
        double acres = farmSize.doubleValue();

        if (acres > 10) {
            result.put("category", "LARGE");
            result.put("colorCode", "#006400");
            result.put("displayName", "Large Farm (>10 acres)");
        } else if (acres >= 5) {
            result.put("category", "MEDIUM");
            result.put("colorCode", "#90EE90");
            result.put("displayName", "Medium Farm (5-10 acres)");
        } else {
            result.put("category", "SMALL");
            result.put("colorCode", "#FFD700");
            result.put("displayName", "Small Farm (<5 acres)");
        }
        return result;
    }

    /**
     * Register a new farmer
     * Status starts as PENDING_APPROVAL
     */
    public Farmer registerFarmer(Farmer farmer) {
        // Validate unique National ID
        if (farmerRepository.existsByNationalId(farmer.getNationalId())) {
            throw new RuntimeException("National ID already registered");
        }
        
        // Validate unique Phone number
        if (farmerRepository.existsByPhone(farmer.getPhone())) {
            throw new RuntimeException("Phone number already registered");
        }

        // Determine farm size category and color code
        Map<String, String> sizeInfo = determineFarmSizeCategory(farmer.getFarmSize());
        farmer.setFarmSizeCategory(sizeInfo.get("category"));
        farmer.setColorCode(sizeInfo.get("colorCode"));
        
        // Set status and timestamps
        farmer.setStatus("PENDING_APPROVAL");
        farmer.setRegisteredAt(LocalDateTime.now());
        farmer.setCreatedAt(LocalDateTime.now());

        return farmerRepository.save(farmer);
    }

    
     //Get farmer by ID
     
    public Farmer getFarmerById(Long farmerId) {
        return farmerRepository.findById(farmerId)
                .orElseThrow(() -> new RuntimeException("Farmer not found with ID: " + farmerId));
    }

    // Get farmer status with user-friendly message
     
    public Map<String, Object> getFarmerStatus(Long farmerId) {
        Farmer farmer = getFarmerById(farmerId);
        
        Map<String, Object> status = new HashMap<>();
        status.put("id", farmer.getId());
        status.put("name", farmer.getName());
        status.put("status", farmer.getStatus());
        status.put("farmSizeCategory", farmer.getFarmSizeCategory());
        status.put("colorCode", farmer.getColorCode());
        status.put("registeredAt", farmer.getRegisteredAt());
        status.put("approvedAt", farmer.getApprovedAt());
        
        // Add user-friendly message based on status
        if ("PENDING_APPROVAL".equals(farmer.getStatus())) {
            status.put("message", "Your registration is pending approval. A field officer will contact you within 48 hours.");
            status.put("nextSteps", "Wait for field officer to visit your farm for verification.");
        } else if ("APPROVED".equals(farmer.getStatus())) {
            status.put("message", "Your farm has been verified! You can now view your deliveries.");
            status.put("nextSteps", "You can now view your delivery history in the app.");
        } else if ("REJECTED".equals(farmer.getStatus())) {
            String rejectionMsg = farmer.getRejectionReason() != null ? farmer.getRejectionReason() : "No reason provided";
            status.put("message", "Your registration was rejected. Reason: " + rejectionMsg);
            status.put("nextSteps", "Please re-register with corrected information.");
        }
        
        return status;
    }
}