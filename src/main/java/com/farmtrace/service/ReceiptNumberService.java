package com.farmtrace.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReceiptNumberService {

    @PersistenceContext
    private EntityManager entityManager;

    public String generateReceiptNumber() {
        Number nextVal = (Number) entityManager
                .createNativeQuery("SELECT nextval('receipt_number_seq')")
                .getSingleResult();
        return String.format("FT-%06d", nextVal.longValue());
    }
}
