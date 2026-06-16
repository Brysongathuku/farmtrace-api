package com.farmtrace.service;

import com.farmtrace.dto.request.CreateClerkRequest;
import com.farmtrace.dto.response.UserResponse;
import com.farmtrace.model.User;
import com.farmtrace.enums.FarmerStatus;
import com.farmtrace.enums.Role;
import com.farmtrace.exception.ConflictException;
import com.farmtrace.exception.ResourceNotFoundException;
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
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public void createClerk(CreateClerkRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already in use");
        }

        User clerk = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getDefaultPassword()))
                .role(Role.CLERK)
                .status(FarmerStatus.APPROVED)
                .forcePasswordChange(true)
                .build();
        userRepository.save(clerk);

        // Send email with credentials
        emailService.sendClerkCredentials(request.getEmail(), request.getDefaultPassword());
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
                .name(user.getEmail())  // ← CHANGED: using email instead of fullName
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .build();
    }
}
