package com.farmtrace.repository;

import com.farmtrace.model.CollectionCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CollectionCenterRepository extends JpaRepository<CollectionCenter, UUID> {

    List<CollectionCenter> findByCooperativeId(UUID cooperativeId);
}
