package com.farmtrace.controller;

import com.farmtrace.entity.User;
import com.farmtrace.repository.UserRepository;
import com.farmtrace.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Login endpoint - Mobile app will call this
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        // Validate input
        if (email == null || password == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Email and password are required");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        User user = userOptional.get();

        // Check password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // Check if user is active
        if (!user.isActive()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Account is deactivated. Please contact administrator.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        // Generate JWT token
        String token = jwtService.generateToken(user.getEmail(), user.getId(), user.getRole());

        // Prepare success response
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login successful");
        
        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", token);
        data.put("tokenType", "Bearer");
        data.put("expiresIn", 36000); // 10 hours in seconds
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("role", user.getRole());
        userData.put("cooperativeId", user.getCooperativeId());
        
        data.put("user", userData);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    // Register test user (for development only)
    @PostMapping("/register-test-user")
    public ResponseEntity<Map<String, Object>> registerTestUser() {
        // Check if test user already exists
        if (userRepository.existsByEmail("test@farmtrace.com")) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Test user already exists");
            return ResponseEntity.badRequest().body(response);
        }

        // Create test user with FIELD_OFFICER role
        User testUser = new User();
        testUser.setEmail("test@farmtrace.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setName("Test Field Officer");
        testUser.setRole("FIELD_OFFICER");
        testUser.setActive(true);

        userRepository.save(testUser);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Test user created successfully");
        
        Map<String, Object> data = new HashMap<>();
        data.put("email", "test@farmtrace.com");
        data.put("password", "password123");
        data.put("role", "FIELD_OFFICER");
        response.put("data", data);
        
        return ResponseEntity.ok(response);
    }
}