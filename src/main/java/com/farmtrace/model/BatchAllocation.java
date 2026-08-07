package com.farmtrace.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "batch_allocations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @ManyToOne
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;

    // How much of this delivery's quantity went into this specific batch —
    // usually the whole delivery, but can be a partial amount when a
    // delivery overflows a bag and spills into the next one.
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal allocatedKg;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
