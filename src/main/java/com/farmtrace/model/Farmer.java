package com.farmtrace.model;

import com.farmtrace.enums.CropType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "farmers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Farmer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String nationalId;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal farmSizeAcres;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CropType cropType;

    @Column(nullable = false)
    private String county;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal gpsLatitude;

    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal gpsLongitude;

    @ManyToOne
    @JoinColumn(name = "cooperative_id")
    private Cooperative cooperative;

    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL)
    @Builder.Default
    private List<FarmPhoto> photos = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime registeredAt;
}
