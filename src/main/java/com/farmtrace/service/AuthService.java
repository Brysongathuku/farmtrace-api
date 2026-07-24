package com.farmtrace.service;

import com.farmtrace.dto.request.ChangePasswordRequest;
import com.farmtrace.dto.request.LoginRequest;
import com.farmtrace.dto.request.RegisterRequest;
import com.farmtrace.dto.response.AuthResponse;
import com.farmtrace.model.*;
import com.farmtrace.enums.FarmerStatus;
import com.farmtrace.enums.Role;
import com.farmtrace.exception.BadRequestException;
import com.farmtrace.exception.ConflictException;
import com.farmtrace.exception.ResourceNotFoundException;
import com.farmtrace.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepo;
    private final FarmerRepository farmerRepo;
    private final CooperativeRepository cooperativeRepo;
    private final EmailVerificationTokenRepository tokenRepo;
    private final FarmPhotoRepository farmPhotoRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final FileStorageService fileStorage;
    private final AuthenticationManager authManager;
    private final AuditLogService auditLogService;

    // ── REGISTER FARMER ─────────────────────────────────────────
    public void registerFarmer(RegisterRequest req, List<MultipartFile> photos) {

        if (userRepo.existsByEmail(req.getEmail()))
            throw new ConflictException("Email already registered");

        if (farmerRepo.existsByNationalId(req.getNationalId()))
            throw new ConflictException("National ID already registered");

        User user = User.builder()
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(Role.FARMER)
                .status(FarmerStatus.PENDING)
                .forcePasswordChange(false)
                .build();
        userRepo.save(user);

        Cooperative coop = cooperativeRepo.findById(req.getCooperativeId())
                .orElseThrow(() -> new ResourceNotFoundException("Cooperative not found"));

        Farmer farmer = Farmer.builder()
                .user(user)
                .fullName(req.getFullName())
                .nationalId(req.getNationalId())
                .phone(req.getPhone())
                .farmSizeAcres(req.getFarmSizeAcres())
                .cropType(req.getCropType())
                .county(req.getCounty())
                .location(req.getLocation())
                .gpsLatitude(req.getGpsLatitude())
                .gpsLongitude(req.getGpsLongitude())
                .cooperative(coop)
                .build();
        farmerRepo.save(farmer);

        if (photos != null && !photos.isEmpty()) {
            for (MultipartFile photo : photos) {
                String url = fileStorage.uploadFile(photo, farmer.getId());
                FarmPhoto fp = FarmPhoto.builder()
                        .farmer(farmer)
                        .fileUrl(url)
                        .build();
                farmPhotoRepo.save(fp);
            }
        }

        // ✅ TESTING: email skipped — re-enable when Gmail App Password is configured
        // emailService.sendVerificationEmail(user);
    }

    // ── LOGIN ────────────────────────────────────────────────────
    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // ✅ Block farmers from logging in until approved by a clerk
        if (user.getRole() == Role.FARMER) {
            if (user.getStatus() == FarmerStatus.PENDING) {
                throw new BadRequestException("Your account is pending approval by a clerk.");
            }
            if (user.getStatus() == FarmerStatus.REJECTED) {
                throw new BadRequestException("Your account has been rejected. Please contact support.");
            }
            if (user.getStatus() == FarmerStatus.UNVERIFIED) {
                throw new BadRequestException("Your account is not verified yet.");
            }
        }

        String token = jwtService.generateToken(user);

        if (user.getRole() == Role.ADMIN) {
            auditLogService.log(
                    "ADMIN_LOGIN",
                    user.getEmail(),
                    "ADMIN",
                    "Admin logged in"
            );
        }

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .forcePasswordChange(user.isForcePasswordChange())
                .hasUnreadNotification(user.isHasUnreadNotification())
                .build();
    }

    // ── VERIFY EMAIL ─────────────────────────────────────────────
    public void verifyEmail(String token) {
        EmailVerificationToken evt = tokenRepo.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid token"));

        if (evt.isUsed())
            throw new BadRequestException("Token already used");

        if (evt.getExpiresAt().isBefore(java.time.LocalDateTime.now()))
            throw new BadRequestException("Token expired");

        User user = evt.getUser();
        user.setStatus(FarmerStatus.PENDING);
        userRepo.save(user);

        evt.setUsed(true);
        tokenRepo.save(evt);
    }

    // ── CHANGE PASSWORD ───────────────────────────────────────────
    public void changePassword(User user, ChangePasswordRequest req) {
        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        user.setForcePasswordChange(false);
        userRepo.save(user);
    }
}