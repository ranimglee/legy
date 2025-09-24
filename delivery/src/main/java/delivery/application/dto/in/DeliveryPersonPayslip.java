package delivery.application.dto.in;


import java.util.Date;

public record DeliveryPersonPayslip(
        String deliveryPersonId,
        String deliveryPersonName,
        int totalDeliveries,
        double totalDistanceKm,
        double totalDeliveryDurationMinutes,
        double totalPayout,
        double bonus,
        double penalty,
        double finalPayout,
        Date periodStart,
        Date periodEnd,
        Date generatedAt,       // NEW: Date of generation
        String reference
) {}
