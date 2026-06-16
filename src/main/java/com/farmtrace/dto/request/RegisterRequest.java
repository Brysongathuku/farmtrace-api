package com.farmtrace.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;
import com.farmtrace.enums.CropType;

@Data
public class RegisterRequest {

    @NotBlank
    private String fullName;

    @NotBlank
    private String nationalId;

    @Email
    @NotBlank
    private String email;

    @Size(min = 6)
    @NotBlank
    private String password;

    @NotBlank
    private String phone;

    @NotNull
    @Positive
    private BigDecimal farmSizeAcres;

    @NotNull
    private CropType cropType;

    @NotBlank
    private String county;

    @NotBlank
    private String location;

    @NotNull
    private UUID cooperativeId;

    @NotNull
    private BigDecimal gpsLatitude;

    @NotNull
    private BigDecimal gpsLongitude;
}
