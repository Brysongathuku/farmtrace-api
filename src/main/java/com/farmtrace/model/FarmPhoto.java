package com.farmtrace.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "farm_photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @Column(nullable = false)
    private String fileUrl;

    @CreationTimestamp
    private LocalDateTime uploadedAt;
}
