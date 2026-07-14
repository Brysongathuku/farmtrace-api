package com.farmtrace.dto.response;

import com.farmtrace.enums.CropType;
import com.farmtrace.enums.FarmerStatus;
import com.farmtrace.model.Farmer;
import com.farmtrace.model.FarmPhoto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
public class FarmerResponse {

    private UUID farmerId;
    private UUID userId;
    private String fullName;
    private String nationalId;
    private String email;
    private String phone;
    private BigDecimal farmSizeAcres;
    private CropType cropType;
    private String county;
    private String location;
    private BigDecimal gpsLatitude;
    private BigDecimal gpsLongitude;
    private FarmerStatus status;
    private LocalDateTime registeredAt;
    private List<String> photoUrls;

    public static FarmerResponse from(Farmer farmer) {
        return FarmerResponse.builder()
                .farmerId(farmer.getId())
                .userId(farmer.getUser().getId())
                .fullName(farmer.getFullName())
                .nationalId(farmer.getNationalId())
                .email(farmer.getUser().getEmail())
                .phone(farmer.getPhone())
                .farmSizeAcres(farmer.getFarmSizeAcres())
                .cropType(farmer.getCropType())
                .county(farmer.getCounty())
                .location(farmer.getLocation())
                .gpsLatitude(farmer.getGpsLatitude())
                .gpsLongitude(farmer.getGpsLongitude())
                .status(farmer.getUser().getStatus())
                .registeredAt(farmer.getRegisteredAt())
                .photoUrls(farmer.getPhotos().stream()
                        .map(FarmPhoto::getFileUrl)
                        .collect(Collectors.toList()))
                .build();
    }
}