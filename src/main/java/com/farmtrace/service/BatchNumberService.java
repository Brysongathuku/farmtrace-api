package com.farmtrace.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BatchNumberService {

    @PersistenceContext
    private EntityManager entityManager;

    public String generateBatchNumber() {
        Number nextVal = (Number) entityManager
                .createNativeQuery("SELECT nextval('batch_number_seq')")
                .getSingleResult();
        return String.format("BT-%06d", nextVal.longValue());
    }
}
