package com.farmtrace.model;

import com.farmtrace.enums.Grade;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "grade_prices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradePrice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Grade grade;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerKg;

    @Column(nullable = false)
    private String updatedByEmail;

    @CreationTimestamp
    private LocalDateTime effectiveFrom;
}
