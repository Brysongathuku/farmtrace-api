package com.farmtrace.repository;

import com.farmtrace.model.BatchAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BatchAllocationRepository extends JpaRepository<BatchAllocation, UUID> {

    List<BatchAllocation> findByBatch_Id(UUID batchId);

    List<BatchAllocation> findByDelivery_Id(UUID deliveryId);
}
