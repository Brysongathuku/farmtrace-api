package com.farmtrace.service;

import com.farmtrace.dto.request.CreateClerkRequest;
import com.farmtrace.dto.response.UserResponse;
import com.farmtrace.exception.ConflictException;
import com.farmtrace.exception.ResourceNotFoundException;
import com.farmtrace.enums.Role;
import com.farmtrace.model.Cooperative;
import com.farmtrace.model.User;
import com.farmtrace.repository.CooperativeRepository;
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
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

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

        try {
            emailService.sendClerkCredentials(request.getEmail(), request.getDefaultPassword());
        } catch (Exception e) {
            // ✅ Don't let a broken mail server block clerk creation — log and move on.
            // Re-enable strict failure once Gmail App Password is configured.
            System.err.println("⚠️ Failed to email clerk credentials: " + e.getMessage());
        }
    }

    public List<UserResponse> getAllClerks() {
        return userRepository.findByRole(Role.CLERK).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public void deleteClerk(UUID id) {
        User clerk = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clerk not found"));
        userRepository.delete(clerk);
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
}