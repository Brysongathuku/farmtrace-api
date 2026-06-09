package com.farmtrace.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "receipt_number", unique = true)
    private String receiptNumber;

    @Column(name = "farmer_id", nullable = false)
    private Long farmerId;

    @Column(name = "clerk_id", nullable = false)
    private Long clerkId;

    @Column(name = "cooperative_id")
    private Long cooperativeId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal weight;

    @Column(length = 1)
    private String grade;

    @Column(precision = 5, scale = 2)
    private BigDecimal moisture;

    @Column(name = "price_per_kg", precision = 10, scale = 2)
    private BigDecimal pricePerKg;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "collection_center")
    private String collectionCenter;

    @Column(name = "quality_notes")
    private String qualityNotes;

    @Column(name = "receipt_pdf_url")
    private String receiptPdfUrl;

    @Column(name = "qr_code_url")
    private String qrCodeUrl;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @Column(name = "is_voided")
    private boolean isVoided = false;

    @Column(name = "void_reason")
    private String voidReason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }

    public Long getFarmerId() { return farmerId; }
    public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }

    public Long getClerkId() { return clerkId; }
    public void setClerkId(Long clerkId) { this.clerkId = clerkId; }

    public Long getCooperativeId() { return cooperativeId; }
    public void setCooperativeId(Long cooperativeId) { this.cooperativeId = cooperativeId; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public BigDecimal getMoisture() { return moisture; }
    public void setMoisture(BigDecimal moisture) { this.moisture = moisture; }

    public BigDecimal getPricePerKg() { return pricePerKg; }
    public void setPricePerKg(BigDecimal pricePerKg) { this.pricePerKg = pricePerKg; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getCollectionCenter() { return collectionCenter; }
    public void setCollectionCenter(String collectionCenter) { this.collectionCenter = collectionCenter; }

    public String getQualityNotes() { return qualityNotes; }
    public void setQualityNotes(String qualityNotes) { this.qualityNotes = qualityNotes; }

    public String getReceiptPdfUrl() { return receiptPdfUrl; }
    public void setReceiptPdfUrl(String receiptPdfUrl) { this.receiptPdfUrl = receiptPdfUrl; }

    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }

    public LocalDateTime getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDateTime deliveryDate) { this.deliveryDate = deliveryDate; }

    public boolean isVoided() { return isVoided; }
    public void setVoided(boolean voided) { isVoided = voided; }

    public String getVoidReason() { return voidReason; }
    public void setVoidReason(String voidReason) { this.voidReason = voidReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}