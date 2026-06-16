package com.farmtrace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String role;
    private String status; // null for ADMIN and CLERK
    private boolean forcePasswordChange;
    private boolean hasUnreadNotification;
}
