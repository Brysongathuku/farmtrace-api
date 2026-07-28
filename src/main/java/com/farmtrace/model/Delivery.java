package com.farmtrace.model;

import com.farmtrace.enums.DeliveryStatus;
import com.farmtrace.enums.Grade;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "deliveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String receiptNumber;

    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @ManyToOne
    @JoinColumn(name = "collection_center_id", nullable = false)
    private CollectionCenter collectionCenter;

    @ManyToOne
    @JoinColumn(name = "recorded_by_clerk_id", nullable = false)
    private User recordedByClerk;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Grade grade;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal moistureContent;

    // Snapshotted at the moment of delivery, so a later price change never
    // alters the value shown on an already-issued receipt.
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerKgAtDelivery;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(length = 500, nullable = true)
    private String rejectionReason;

    @CreationTimestamp
    private LocalDateTime deliveryTimestamp;
}
