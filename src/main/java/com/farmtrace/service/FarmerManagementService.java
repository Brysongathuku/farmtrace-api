package com.farmtrace.service;

import com.farmtrace.dto.response.FarmerResponse;
import com.farmtrace.enums.FarmerStatus;
import com.farmtrace.exception.BadRequestException;
import com.farmtrace.exception.ForbiddenException;
import com.farmtrace.exception.ResourceNotFoundException;
import com.farmtrace.model.Farmer;
import com.farmtrace.model.User;
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
    private final AuditLogService auditLogService;

    // ── GET ALL FARMERS (scoped to clerk's cooperative) ───────────
    public List<FarmerResponse> getAllFarmers(User currentUser) {
        UUID cooperativeId = currentUser.getCooperative().getId();
        return farmerRepo.findAllFarmersByCooperativeId(cooperativeId)
                .stream()
                .map(FarmerResponse::from)
                .collect(Collectors.toList());
    }

    // ── GET PENDING FARMERS (scoped to clerk's cooperative) ───────
    public List<FarmerResponse> getPendingFarmers(User currentUser) {
        UUID cooperativeId = currentUser.getCooperative().getId();
        return farmerRepo.findByUserStatusAndCooperativeId(FarmerStatus.PENDING, cooperativeId)
                .stream()
                .map(FarmerResponse::from)
                .collect(Collectors.toList());
    }

    // ── ADMIN: GET ALL FARMERS ACROSS ALL COOPERATIVES ────────────
    public List<FarmerResponse> getAllFarmersForAdmin(FarmerStatus status, UUID cooperativeId) {
        List<Farmer> farmers;

        if (status != null && cooperativeId != null) {
            farmers = farmerRepo.findByUserStatusAndCooperativeId(status, cooperativeId);
        } else if (status != null) {
            farmers = farmerRepo.findByUserStatus(status);
        } else if (cooperativeId != null) {
            farmers = farmerRepo.findAllFarmersByCooperativeId(cooperativeId);
        } else {
            farmers = farmerRepo.findAllFarmers();
        }

        return farmers.stream()
                .map(FarmerResponse::from)
                .collect(Collectors.toList());
    }

    // ── APPROVE FARMER ───────────────────────────────────────────
    public FarmerResponse approveFarmer(UUID farmerId, User currentUser) {
        Farmer farmer = farmerRepo.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found"));

        validateSameCooperative(farmer, currentUser);

        if (farmer.getUser().getStatus() == FarmerStatus.APPROVED) {
            throw new BadRequestException("Farmer is already approved");
        }

        farmer.getUser().setStatus(FarmerStatus.APPROVED);
        userRepo.save(farmer.getUser());

        auditLogService.log(
                "APPROVE_FARMER",
                currentUser.getEmail(),
                "FARMER",
                "Approved farmer " + farmer.getFullName()
        );

        return FarmerResponse.from(farmer);
    }

    // ── REJECT FARMER ────────────────────────────────────────────
    public FarmerResponse rejectFarmer(UUID farmerId, User currentUser, String reason) {
        Farmer farmer = farmerRepo.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found"));

        validateSameCooperative(farmer, currentUser);

        if (farmer.getUser().getStatus() == FarmerStatus.REJECTED) {
            throw new BadRequestException("Farmer is already rejected");
        }

        farmer.getUser().setStatus(FarmerStatus.REJECTED);
        userRepo.save(farmer.getUser());

        auditLogService.log(
                "REJECT_FARMER",
                currentUser.getEmail(),
                "FARMER",
                "Rejected farmer " + farmer.getFullName() + " — reason: " + reason
        );

        return FarmerResponse.from(farmer);
    }

    // ── GUARD: clerk can only act on farmers in their own cooperative ──
    private void validateSameCooperative(Farmer farmer, User currentUser) {
        if (currentUser.getCooperative() == null
                || farmer.getCooperative() == null
                || !farmer.getCooperative().getId().equals(currentUser.getCooperative().getId())) {
            throw new ForbiddenException("You can only manage farmers in your own cooperative");
        }
    }
}