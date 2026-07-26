package com.farmtrace.service;

import com.farmtrace.dto.response.FarmerResponse;
import com.farmtrace.model.Farmer;
import com.farmtrace.model.User;
import com.farmtrace.repository.FarmerRepository;
import com.farmtrace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FarmerService {

    private final FarmerRepository farmerRepository;
    private final UserRepository userRepository;

    public FarmerResponse getFarmerProfile(UUID userId) {
        Farmer farmer = farmerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        return FarmerResponse.from(farmer);
    }

    public void acknowledgeNotification(UUID userId) {
        Farmer farmer = farmerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        User user = farmer.getUser();
        user.setHasUnreadNotification(false);
        userRepository.save(user);
    }
}