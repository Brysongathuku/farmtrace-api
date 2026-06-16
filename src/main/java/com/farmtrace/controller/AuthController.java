package com.farmtrace.controller;

import com.farmtrace.dto.request.ChangePasswordRequest;
import com.farmtrace.dto.request.LoginRequest;
import com.farmtrace.dto.request.RegisterRequest;
import com.farmtrace.dto.response.ApiResponse;
import com.farmtrace.dto.response.AuthResponse;
import com.farmtrace.model.User;
import com.farmtrace.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        authService.registerFarmer(request, null);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Registration successful. Awaiting admin approval."));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(new ApiResponse("Email verified. Awaiting approval."));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal User currentUser) {
        authService.changePassword(currentUser, request);
        return ResponseEntity.ok(new ApiResponse("Password updated."));
    }

    // ✅ TEMPORARY: generate BCrypt hash — DELETE after use
    @GetMapping("/hash")
    public ResponseEntity<String> generateHash() {
        return ResponseEntity.ok(new BCryptPasswordEncoder().encode("password123"));
    }
}