package com.farmtrace.repository;

import com.farmtrace.enums.DeliveryStatus;
import com.farmtrace.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    List<Delivery> findByFarmer_IdOrderByDeliveryTimestampDesc(UUID farmerId);

    List<Delivery> findByCollectionCenter_IdOrderByDeliveryTimestampDesc(UUID collectionCenterId);

    List<Delivery> findByStatusOrderByDeliveryTimestampDesc(DeliveryStatus status);
}