package com.farmtrace.repository;

import com.farmtrace.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    // Find all deliveries for a specific farmer
    List<Delivery> findByFarmerId(Long farmerId);

    // Find all deliveries for a specific clerk
    List<Delivery> findByClerkId(Long clerkId);

    // Find delivery by receipt number
    Optional<Delivery> findByReceiptNumber(String receiptNumber);

    // Check if receipt number already exists
    boolean existsByReceiptNumber(String receiptNumber);

    // Find all deliveries for a cooperative
    List<Delivery> findByCooperativeId(Long cooperativeId);

    // Find all non-voided deliveries for a farmer
    List<Delivery> findByFarmerIdAndIsVoided(Long farmerId, boolean isVoided);
}