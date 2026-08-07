package com.farmtrace.model;

import com.farmtrace.enums.BatchStatus;
import com.farmtrace.enums.Grade;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "batches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String batchNumber;

    @ManyToOne
    @JoinColumn(name = "collection_center_id", nullable = false)
    private CollectionCenter collectionCenter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Grade grade;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal capacityKg;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal currentWeightKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BatchStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime dispatchedAt;
}
