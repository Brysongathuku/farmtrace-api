package com.farmtrace.repository;

import com.farmtrace.enums.BatchStatus;
import com.farmtrace.enums.Grade;
import com.farmtrace.model.Batch;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BatchRepository extends JpaRepository<Batch, UUID> {

    // PESSIMISTIC_WRITE takes a row-level lock (SELECT ... FOR UPDATE) so that
    // if two clerks at the same center record the same grade at the same
    // moment, the second transaction blocks and waits instead of both reading
    // the same currentWeightKg and racing to overwrite each other's update.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Batch b WHERE b.collectionCenter.id = :centerId " +
           "AND b.grade = :grade AND b.status = com.farmtrace.enums.BatchStatus.OPEN")
    Optional<Batch> findOpenBatchForUpdate(
            @Param("centerId") UUID centerId,
            @Param("grade") Grade grade
    );

    List<Batch> findByCollectionCenter_IdOrderByCreatedAtDesc(UUID collectionCenterId);

    List<Batch> findByCollectionCenter_IdAndStatusOrderByCreatedAtDesc(UUID collectionCenterId, BatchStatus status);
}
