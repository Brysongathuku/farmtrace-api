package com.farmtrace.service;

import com.farmtrace.model.EmailVerificationToken;
import com.farmtrace.model.User;
import com.farmtrace.repository.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationTokenRepository tokenRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();

        EmailVerificationToken evt = EmailVerificationToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .used(false)
                .build();
        tokenRepository.save(evt);

        String link = baseUrl + "/api/auth/verify-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("FarmTrace - Verify Your Email");
        message.setText("Hello,\n\nPlease verify your email by clicking the link below:\n" +
                link + "\n\nThis link expires in 24 hours.\n\nFarmTrace Team");

        mailSender.send(message);
    }

    public void sendClerkCredentials(String email, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("FarmTrace - Your Clerk Account");
        message.setText("Hello,\n\nYour FarmTrace clerk account has been created.\n\n" +
                "Email: " + email + "\n" +
                "Temporary Password: " + password + "\n\n" +
                "You will be required to change your password on first login.\n\nFarmTrace Team");

        mailSender.send(message);
    }
}