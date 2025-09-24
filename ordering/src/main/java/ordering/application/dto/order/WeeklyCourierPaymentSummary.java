package ordering.application.dto.order;

public record WeeklyCourierPaymentSummary(
        String deliveryPersonId,
        String deliveryPersonName,
        int totalDeliveries,
        int totalRefusals,
        double totalDeliveryPayout,
        double bonus,
        double penalty,
        double finalPayout,
        int totalOrders ,
        double avgPayoutPerDelivery,
        Boolean isPaid


) {}