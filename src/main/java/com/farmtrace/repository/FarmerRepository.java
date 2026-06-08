
package com.farmtrace.repository;

import com.farmtrace.entity.Farmer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Long> {

    // Find farmer by National ID
    Optional<Farmer> findByNationalId(String nationalId);

    // Find farmer by Phone number
    Optional<Farmer> findByPhone(String phone);

    // Check if National ID already exists
    boolean existsByNationalId(String nationalId);

    // Check if Phone number already exists
    boolean existsByPhone(String phone);

    // Find farmers by status (PENDING_APPROVAL, APPROVED, REJECTED)
    List<Farmer> findByStatus(String status);

    // Find farmers by status with pagination
    Page<Farmer> findByStatus(String status, Pageable pageable);

    // Count pending approvals (for field officer dashboard)
    @Query("SELECT COUNT(f) FROM Farmer f WHERE f.status = 'PENDING_APPROVAL'")
    long countPendingApprovals();

    // Get farm size distribution for dashboard statistics
    @Query("SELECT f.farmSizeCategory, COUNT(f) FROM Farmer f GROUP BY f.farmSizeCategory")
    List<Object[]> getFarmSizeDistribution();
}