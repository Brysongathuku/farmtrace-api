package com.farmtrace.repository;

import com.farmtrace.enums.Grade;
import com.farmtrace.model.GradePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GradePriceRepository extends JpaRepository<GradePrice, UUID> {

    // Most recent price row for a given grade — this IS the "current" price.
    @Query("SELECT gp FROM GradePrice gp WHERE gp.grade = :grade ORDER BY gp.effectiveFrom DESC LIMIT 1")
    Optional<GradePrice> findCurrentPriceForGrade(Grade grade);

    // Full price history, newest first — for admin's price-history view.
    List<GradePrice> findAllByOrderByEffectiveFromDesc();
}