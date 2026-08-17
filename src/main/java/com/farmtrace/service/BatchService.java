package com.farmtrace.service;

import com.farmtrace.enums.BatchStatus;
import com.farmtrace.model.Batch;
import com.farmtrace.model.BatchAllocation;
import com.farmtrace.model.CollectionCenter;
import com.farmtrace.model.Delivery;
import com.farmtrace.model.User;
import com.farmtrace.exception.ForbiddenException;
import com.farmtrace.exception.ResourceNotFoundException;
import com.farmtrace.repository.BatchAllocationRepository;
import com.farmtrace.repository.BatchRepository;
import com.farmtrace.repository.CollectionCenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BatchService {

    // Fixed for all batches, per current requirements. If this ever needs to
    // vary by grade or collection center, move it onto the Batch creation
    // path (e.g. a lookup) rather than changing this constant.
    private static final BigDecimal CAPACITY_KG = new BigDecimal("200.00");

    private final BatchRepository batchRepository;
    private final BatchAllocationRepository batchAllocationRepository;
    private final BatchNumberService batchNumberService;
    private final CollectionCenterRepository collectionCenterRepository;
    private final AuditLogService auditLogService;

    /**
     * Allocates an approved delivery's full quantity into the current open
     * batch(es) for its collection center + grade, spilling into a new batch
     * whenever the current one fills up.
     *
     * Deliberately runs in the SAME transaction as the caller (default
     * REQUIRED propagation), not REQUIRES_NEW. The delivery row this method
     * references was just saved by DeliveryService.recordDelivery() in that
     * same transaction and has not yet been committed — a separate
     * transaction cannot see it yet, so a foreign key insert against
     * delivery_id would fail. Sharing the transaction means the delivery and
     * its batch allocation(s) commit together atomically.
     */
    @Transactional
    public void allocateDelivery(Delivery delivery) {
        BigDecimal remaining = delivery.getQuantityKg();

        while (remaining.compareTo(BigDecimal.ZERO) > 0) {
            Batch batch = batchRepository
                    .findOpenBatchForUpdate(delivery.getCollectionCenter().getId(), delivery.getGrade())
                    .orElseGet(() -> batchRepository.save(
                            Batch.builder()
                                    .batchNumber(batchNumberService.generateBatchNumber())
                                    .collectionCenter(delivery.getCollectionCenter())
                                    .grade(delivery.getGrade())
                                    .capacityKg(CAPACITY_KG)
                                    .currentWeightKg(BigDecimal.ZERO)
                                    .status(BatchStatus.OPEN)
                                    .build()
                    ));

            BigDecimal spaceLeft = batch.getCapacityKg().subtract(batch.getCurrentWeightKg());
            BigDecimal allocateAmount = remaining.min(spaceLeft);

            batchAllocationRepository.save(
                    BatchAllocation.builder()
                            .batch(batch)
                            .delivery(delivery)
                            .allocatedKg(allocateAmount)
                            .build()
            );

            batch.setCurrentWeightKg(batch.getCurrentWeightKg().add(allocateAmount));
            if (batch.getCurrentWeightKg().compareTo(batch.getCapacityKg()) >= 0) {
                batch.setStatus(BatchStatus.FULL);
            }
            batchRepository.save(batch);

            remaining = remaining.subtract(allocateAmount);
        }
    }

    // ── Read/query methods (cooperative-scoped) ─────────────────────

    public List<Batch> getBatchesForCenter(UUID collectionCenterId, User clerk) {
        CollectionCenter center = requireOwnCooperativeCenter(collectionCenterId, clerk);
        return batchRepository.findByCollectionCenter_IdOrderByCreatedAtDesc(center.getId());
    }

    public Batch getBatchOrThrow(UUID batchId, User clerk) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));
        requireOwnCooperativeCenter(batch.getCollectionCenter().getId(), clerk);
        return batch;
    }

    // The traceability view: every farmer, receipt, and exact kg contributed
    // to this specific bag — what a quality issue investigation actually needs.
    public List<BatchAllocation> getAllocationsForBatch(UUID batchId, User clerk) {
        getBatchOrThrow(batchId, clerk); // enforces the cooperative guard
        return batchAllocationRepository.findByBatch_Id(batchId);
    }

    /**
     * Marks a batch as DISPATCHED — called by a clerk at the collection
     * center when the truck actually collects the bags. Allowed from either
     * OPEN or FULL, since a batch doesn't always fill to exactly 200kg
     * before it needs to leave (end of day, last collection before a truck
     * arrives, etc.) — only blocked if it's already been dispatched.
     * Cooperative-scoped like the read methods above, and audit-logged
     * since dispatch is a meaningful milestone worth an accountability trail.
     */
    @Transactional
    public Batch markDispatched(UUID batchId, User clerk) {
        Batch batch = getBatchOrThrow(batchId, clerk); // enforces the cooperative guard

        if (batch.getStatus() == BatchStatus.DISPATCHED) {
            throw new com.farmtrace.exception.BadRequestException("This batch has already been dispatched");
        }

        batch.setStatus(BatchStatus.DISPATCHED);
        batch.setDispatchedAt(LocalDateTime.now());
        Batch saved = batchRepository.save(batch);

        auditLogService.log(
                "DISPATCH_BATCH",
                clerk.getEmail(),
                "BATCH",
                "Batch " + saved.getBatchNumber() + " (" + saved.getCurrentWeightKg() + "kg / "
                        + saved.getCapacityKg() + "kg, Grade " + saved.getGrade() + ") dispatched from "
                        + saved.getCollectionCenter().getName()
        );

        return saved;
    }

    private CollectionCenter requireOwnCooperativeCenter(UUID collectionCenterId, User clerk) {
        CollectionCenter center = collectionCenterRepository.findById(collectionCenterId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection center not found"));
        if (clerk.getCooperative() == null
                || !center.getCooperative().getId().equals(clerk.getCooperative().getId())) {
            throw new ForbiddenException("You can only view batches within your own cooperative");
        }
        return center;
    }
}