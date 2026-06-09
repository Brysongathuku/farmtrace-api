package com.farmtrace.service;

import com.farmtrace.entity.Delivery;
import com.farmtrace.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private FarmerService farmerService;

    /**
     * Generate a unique receipt number
     * Format: RCP-YYYYMMDD-XXXX (e.g. RCP-20260609-0001)
     */
    private String generateReceiptNumber() {
        String datePart = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%04d",
                (int)(Math.random() * 9000) + 1000);
        return "RCP-" + datePart + "-" + randomPart;
    }

    /**
     * Record a new delivery - ONLY clerks can do this
     */
    public Delivery recordDelivery(Delivery delivery) {

        // Make sure the farmer exists
        farmerService.getFarmerById(delivery.getFarmerId());

        // Generate unique receipt number
        String receiptNumber;
        do {
            receiptNumber = generateReceiptNumber();
        } while (deliveryRepository.existsByReceiptNumber(receiptNumber));
        delivery.setReceiptNumber(receiptNumber);

        // Calculate total amount = weight x price per kg
        if (delivery.getWeight() != null && delivery.getPricePerKg() != null) {
            BigDecimal total = delivery.getWeight()
                    .multiply(delivery.getPricePerKg());
            delivery.setTotalAmount(total);
        }

        // Set delivery date and timestamps
        delivery.setDeliveryDate(LocalDateTime.now());
        delivery.setCreatedAt(LocalDateTime.now());
        delivery.setUpdatedAt(LocalDateTime.now());
        delivery.setVoided(false);

        return deliveryRepository.save(delivery);
    }

    /**
     * Get all deliveries (for clerk and manager)
     */
    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    /**
     * Get a specific delivery by ID
     */
    public Delivery getDeliveryById(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Delivery not found with ID: " + id));
    }

    /**
     * Get all deliveries for a specific farmer
     */
    public List<Delivery> getDeliveriesByFarmer(Long farmerId) {
        // Make sure farmer exists first
        farmerService.getFarmerById(farmerId);
        return deliveryRepository.findByFarmerIdAndIsVoided(farmerId, false);
    }

    /**
     * Get delivery by receipt number
     */
    public Delivery getDeliveryByReceiptNumber(String receiptNumber) {
        return deliveryRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new RuntimeException(
                        "Receipt not found: " + receiptNumber));
    }

    /**
     * Void a delivery - ONLY managers can do this
     */
    public Delivery voidDelivery(Long id, String reason) {
        Delivery delivery = getDeliveryById(id);

        if (delivery.isVoided()) {
            throw new RuntimeException(
                    "Delivery is already voided");
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new RuntimeException(
                    "A reason is required to void a delivery");
        }

        delivery.setVoided(true);
        delivery.setVoidReason(reason);
        delivery.setUpdatedAt(LocalDateTime.now());

        return deliveryRepository.save(delivery);
    }

    /**
     * Get delivery summary for a farmer
     */
    public Map<String, Object> getFarmerDeliverySummary(Long farmerId) {
        List<Delivery> deliveries = getDeliveriesByFarmer(farmerId);

        BigDecimal totalWeight = deliveries.stream()
                .map(Delivery::getWeight)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmount = deliveries.stream()
                .map(Delivery::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> summary = new HashMap<>();
        summary.put("farmerId", farmerId);
        summary.put("totalDeliveries", deliveries.size());
        summary.put("totalWeight", totalWeight);
        summary.put("totalAmount", totalAmount);
        summary.put("deliveries", deliveries);

        return summary;
    }
}