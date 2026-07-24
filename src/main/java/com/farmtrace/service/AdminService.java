package com.farmtrace.service;

import com.farmtrace.dto.request.CreateClerkRequest;
import com.farmtrace.dto.response.DashboardResponse;
import com.farmtrace.dto.response.UserResponse;
import com.farmtrace.exception.ConflictException;
import com.farmtrace.exception.ResourceNotFoundException;
import com.farmtrace.enums.FarmerStatus;
import com.farmtrace.enums.Role;
import com.farmtrace.model.Cooperative;
import com.farmtrace.model.User;
import com.farmtrace.repository.CooperativeRepository;
import com.farmtrace.repository.FarmerRepository;
import com.farmtrace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final CooperativeRepository cooperativeRepository;
    private final FarmerRepository farmerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuditLogService auditLogService;

    public void createClerk(CreateClerkRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already in use");
        }

        Cooperative cooperative = cooperativeRepository.findById(request.getCooperativeId())
                .orElseThrow(() -> new ResourceNotFoundException("Cooperative not found"));

        User clerk = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .passwordHash(passwordEncoder.encode(request.getDefaultPassword()))
                .role(Role.CLERK)
                .status(null)
                .cooperative(cooperative)
                .forcePasswordChange(true)
                .build();
        userRepository.save(clerk);

        auditLogService.log(
                "CREATE_CLERK",
                getCurrentAdminEmail(),
                "CLERK",
                "Created clerk account for " + clerk.getFullName() + " (" + clerk.getEmail() + ")"
        );

        try {
            emailService.sendClerkCredentials(request.getEmail(), request.getDefaultPassword());
        } catch (Exception e) {
            System.err.println("⚠️ Failed to email clerk credentials: " + e.getMessage());
        }
    }

    public List<UserResponse> getAllClerks() {
        return userRepository.findByRole(Role.CLERK).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getClerksByCooperative(UUID cooperativeId) {
        return userRepository.findByRoleAndCooperativeId(Role.CLERK, cooperativeId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public void deleteClerk(UUID id) {
        User clerk = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clerk not found"));

        auditLogService.log(
                "DELETE_CLERK",
                getCurrentAdminEmail(),
                "CLERK",
                "Deleted clerk account for " + clerk.getFullName() + " (" + clerk.getEmail() + ")"
        );

        userRepository.delete(clerk);
    }

    public DashboardResponse getDashboardStats() {
        return DashboardResponse.builder()
                .totalClerks(userRepository.countByRole(Role.CLERK))
                .totalCooperatives(cooperativeRepository.count())
                .approvedFarmers(farmerRepository.countByUserStatus(FarmerStatus.APPROVED))
                .pendingFarmers(farmerRepository.countByUserStatus(FarmerStatus.PENDING))
                .rejectedFarmers(farmerRepository.countByUserStatus(FarmerStatus.REJECTED))
                .unverifiedFarmers(farmerRepository.countByUserStatus(FarmerStatus.UNVERIFIED))
                .build();
    }

    private UserResponse convertToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .cooperativeName(user.getCooperative() != null ? user.getCooperative().getName() : null)
                .build();
    }

    // Reads the currently authenticated admin's email from the security context.
    private String getCurrentAdminEmail() {
        Object principal = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            return user.getEmail();
        }
        return "unknown";
    }
}