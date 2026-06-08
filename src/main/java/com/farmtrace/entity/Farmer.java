package com.farmtrace.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "farmers")
public class Farmer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false, length = 20)
    private String nationalId;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(name = "farm_size", nullable = false)
    private BigDecimal farmSize;

    @Column(name = "farm_size_category")
    private String farmSizeCategory;

    @Column(name = "color_code")
    private String colorCode;

    @Column(name = "primary_crop")
    private String primaryCrop;

    @Column(name = "farm_latitude", nullable = false)
    private Double farmLatitude;

    @Column(name = "farm_longitude", nullable = false)
    private Double farmLongitude;

    @Column(nullable = false)
    private String status = "PENDING_APPROVAL";

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "cooperative_id")
    private Long cooperativeId;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    @Column(name = "farm_photo_url")
    private String farmPhotoUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public BigDecimal getFarmSize() { return farmSize; }
    public void setFarmSize(BigDecimal farmSize) { this.farmSize = farmSize; }

    public String getFarmSizeCategory() { return farmSizeCategory; }
    public void setFarmSizeCategory(String farmSizeCategory) { this.farmSizeCategory = farmSizeCategory; }

    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }

    public String getPrimaryCrop() { return primaryCrop; }
    public void setPrimaryCrop(String primaryCrop) { this.primaryCrop = primaryCrop; }

    public Double getFarmLatitude() { return farmLatitude; }
    public void setFarmLatitude(Double farmLatitude) { this.farmLatitude = farmLatitude; }

    public Double getFarmLongitude() { return farmLongitude; }
    public void setFarmLongitude(Double farmLongitude) { this.farmLongitude = farmLongitude; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }

    public Long getCooperativeId() { return cooperativeId; }
    public void setCooperativeId(Long cooperativeId) { this.cooperativeId = cooperativeId; }

    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }

    public String getFarmPhotoUrl() { return farmPhotoUrl; }
    public void setFarmPhotoUrl(String farmPhotoUrl) { this.farmPhotoUrl = farmPhotoUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}