package com.farmtrace.repository;

import com.farmtrace.model.Farmer;
import com.farmtrace.enums.FarmerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, UUID> {

    Optional<Farmer> findByNationalId(String nationalId);

    Optional<Farmer> findByPhone(String phone);

    boolean existsByNationalId(String nationalId);

    boolean existsByPhone(String phone);

    // ✅ ADDED: find farmers by their user status
    @Query("SELECT f FROM Farmer f WHERE f.user.status = :status")
    List<Farmer> findByUserStatus(FarmerStatus status);

    // ✅ ADDED: find all farmers
    @Query("SELECT f FROM Farmer f WHERE f.user.role = com.farmtrace.enums.Role.FARMER")
    List<Farmer> findAllFarmers();
}