package com.farmtrace.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String action; // e.g. CREATE_CLERK, APPROVE_FARMER

    @Column(nullable = false)
    private String actionType; // derived: CREATE, DELETE, APPROVE, REJECT, LOGIN

    @Column(nullable = false)
    private String performedBy; // email of the acting user

    @Column(nullable = false)
    private String targetEntity; // e.g. CLERK, FARMER, COOPERATIVE

    @Column(length = 1000)
    private String details;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
}
