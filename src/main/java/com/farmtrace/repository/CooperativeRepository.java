package com.farmtrace.repository;

import com.farmtrace.model.Cooperative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CooperativeRepository extends JpaRepository<Cooperative, UUID> {

    boolean existsByName(String name);
}
