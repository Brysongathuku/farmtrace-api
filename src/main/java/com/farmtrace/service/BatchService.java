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
import org.springframework.transaction.annotation.Propagation;
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

    /**
     * Allocates an approved delivery's full quantity into the current open
     * batch(es) for its collection center + grade, spilling into a new batch
     * whenever the current one fills up. Must run in its own transaction
     * (REQUIRES_NEW) so the pessimistic lock below is scoped tightly and
     * released as soon as this delivery's allocation is done — not held for
     * the entire recordDelivery() call.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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

    // Admin-only action across any cooperative — deliberately no cooperative
    // guard here, matching how other admin endpoints work.
    @Transactional
    public Batch markDispatched(UUID batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));
        batch.setStatus(BatchStatus.DISPATCHED);
        batch.setDispatchedAt(LocalDateTime.now());
        return batchRepository.save(batch);
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