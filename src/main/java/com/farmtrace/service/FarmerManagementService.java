package com.farmtrace.service;

import com.farmtrace.dto.response.FarmerResponse;
import com.farmtrace.enums.FarmerStatus;
import com.farmtrace.exception.BadRequestException;
import com.farmtrace.exception.ResourceNotFoundException;
import com.farmtrace.model.Farmer;
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
public class FarmerManagementService {

    private final FarmerRepository farmerRepo;
    private final UserRepository userRepo;

    // ── GET ALL FARMERS ──────────────────────────────────────────
    public List<FarmerResponse> getAllFarmers() {
        return farmerRepo.findAllFarmers()
                .stream()
                .map(FarmerResponse::from)
                .collect(Collectors.toList());
    }

    // ── GET PENDING FARMERS ──────────────────────────────────────
    public List<FarmerResponse> getPendingFarmers() {
        return farmerRepo.findByUserStatus(FarmerStatus.PENDING)
                .stream()
                .map(FarmerResponse::from)
                .collect(Collectors.toList());
    }

    // ── APPROVE FARMER ───────────────────────────────────────────
    public FarmerResponse approveFarmer(UUID farmerId) {
        Farmer farmer = farmerRepo.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found"));

        if (farmer.getUser().getStatus() == FarmerStatus.APPROVED) {
            throw new BadRequestException("Farmer is already approved");
        }

        farmer.getUser().setStatus(FarmerStatus.APPROVED);
        userRepo.save(farmer.getUser());

        return FarmerResponse.from(farmer);
    }

    // ── REJECT FARMER ────────────────────────────────────────────
    public FarmerResponse rejectFarmer(UUID farmerId) {
        Farmer farmer = farmerRepo.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found"));

        if (farmer.getUser().getStatus() == FarmerStatus.REJECTED) {
            throw new BadRequestException("Farmer is already rejected");
        }

        farmer.getUser().setStatus(FarmerStatus.REJECTED);
        userRepo.save(farmer.getUser());

        return FarmerResponse.from(farmer);
    }
}
