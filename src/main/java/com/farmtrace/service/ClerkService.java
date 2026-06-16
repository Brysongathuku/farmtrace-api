package com.farmtrace.service;

import com.farmtrace.dto.response.FarmerResponse;
import com.farmtrace.model.Farmer;
import com.farmtrace.model.User;
import com.farmtrace.enums.FarmerStatus;
import com.farmtrace.repository.FarmerRepository;
import com.farmtrace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClerkService {

    private final FarmerRepository farmerRepository;
    private final UserRepository userRepository;

    public List<FarmerResponse> getPendingFarmers() {
        return farmerRepository.findAll().stream()
                .filter(farmer -> farmer.getUser().getStatus() == FarmerStatus.PENDING)
                .map(FarmerResponse::from)  // ← CHANGED: Use static from() method
                .collect(Collectors.toList());
    }

    public List<FarmerResponse> getAllFarmers() {
        return farmerRepository.findAll().stream()
                .map(FarmerResponse::from)  // ← CHANGED: Use static from() method
                .collect(Collectors.toList());
    }

    public void approveFarmer(UUID farmerId) {
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        User user = farmer.getUser();
        user.setStatus(FarmerStatus.APPROVED);
        user.setHasUnreadNotification(true);
        userRepository.save(user);
    }

    public void rejectFarmer(UUID farmerId, String reason) {
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        User user = farmer.getUser();
        user.setStatus(FarmerStatus.REJECTED);
        userRepository.save(user);
    }

    // ← REMOVED: convertToResponse() method (no longer needed)
}
