package ordering.application.dto.order;

import ordering.domain.model.OrderStatus;
import ordering.domain.model.value.DeliveryInfo;
import ordering.domain.model.value.RestaurantInfo;
import shared.enums.PaymentMethod;

import java.util.List;

public record OrderDetailsDTO(
        String orderId,
        List<OrderItemDTO> items,
        double total,
        OrderStatus orderStatus,
        String deliveryAddress,
        DeliveryInfo deliveryInfo,
        RestaurantInfo restaurant,
        PaymentMethod paymentMethod,
        String rejectionReason,
        Double platformPromotionAmount,
        String platformPromotionId,
        Double amountGivenByClient,
        Double changeToReturn,
        String appliedClientPromoCode,
        Double clientPromoDiscount
) {}
