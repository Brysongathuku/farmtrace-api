package com.farmtrace.service;

import com.farmtrace.dto.request.RecordDeliveryRequest;
import com.farmtrace.dto.response.DeliveryResponse;
import com.farmtrace.enums.DeliveryStatus;
import com.farmtrace.exception.BadRequestException;
import com.farmtrace.exception.ForbiddenException;
import com.farmtrace.exception.ResourceNotFoundException;
import com.farmtrace.model.CollectionCenter;
import com.farmtrace.model.Delivery;
import com.farmtrace.model.Farmer;
import com.farmtrace.model.User;
import com.farmtrace.repository.CollectionCenterRepository;
import com.farmtrace.repository.DeliveryRepository;
import com.farmtrace.repository.FarmerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final FarmerRepository farmerRepository;
    private final CollectionCenterRepository collectionCenterRepository;
    private final GradePriceService gradePriceService;
    private final ReceiptNumberService receiptNumberService;
    private final AuditLogService auditLogService;

    public DeliveryResponse recordDelivery(RecordDeliveryRequest request, User clerk) {
        Farmer farmer = farmerRepository.findById(request.getFarmerId())
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found"));

        CollectionCenter center = collectionCenterRepository.findById(request.getCollectionCenterId())
                .orElseThrow(() -> new ResourceNotFoundException("Collection center not found"));

        // Guard: clerk can only record deliveries for their own cooperative.
        if (clerk.getCooperative() == null
                || !farmer.getCooperative().getId().equals(clerk.getCooperative().getId())
                || !center.getCooperative().getId().equals(clerk.getCooperative().getId())) {
            throw new ForbiddenException("You can only record deliveries within your own cooperative");
        }

        if (request.getStatus() == DeliveryStatus.REJECTED
                && (request.getRejectionReason() == null || request.getRejectionReason().isBlank())) {
            throw new BadRequestException("A rejection reason is required when rejecting a delivery");
        }

        BigDecimal pricePerKg = gradePriceService.getCurrentPriceValue(request.getGrade());
        BigDecimal totalValue = request.getQuantityKg().multiply(pricePerKg);

        Delivery delivery = Delivery.builder()
                .receiptNumber(receiptNumberService.generateReceiptNumber())
                .farmer(farmer)
                .collectionCenter(center)
                .recordedByClerk(clerk)
                .quantityKg(request.getQuantityKg())
                .grade(request.getGrade())
                .moistureContent(request.getMoistureContent())
                .pricePerKgAtDelivery(pricePerKg)
                .totalValue(totalValue)
                .status(request.getStatus())
                .rejectionReason(request.getStatus() == DeliveryStatus.REJECTED
                        ? request.getRejectionReason() : null)
                .build();

        Delivery saved = deliveryRepository.save(delivery);

        auditLogService.log(
                request.getStatus() == DeliveryStatus.APPROVED ? "APPROVE_DELIVERY" : "REJECT_DELIVERY",
                clerk.getEmail(),
                "DELIVERY",
                "Receipt " + saved.getReceiptNumber() + " for " + farmer.getFullName()
                        + " (" + request.getQuantityKg() + "kg, Grade " + request.getGrade() + ")"
                        + (request.getStatus() == DeliveryStatus.REJECTED
                                ? " — rejected: " + request.getRejectionReason() : "")
        );

        return DeliveryResponse.from(saved);
    }

    public List<DeliveryResponse> getMyFarmerDeliveries(UUID userId) {
        Farmer farmer = farmerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found"));

        return deliveryRepository.findByFarmer_IdOrderByDeliveryTimestampDesc(farmer.getId()).stream()
                .map(DeliveryResponse::from)
                .collect(Collectors.toList());
    }

    public List<DeliveryResponse> getDeliveriesByCollectionCenter(UUID collectionCenterId) {
        return deliveryRepository.findByCollectionCenter_IdOrderByDeliveryTimestampDesc(collectionCenterId).stream()
                .map(DeliveryResponse::from)
                .collect(Collectors.toList());
    }
}