package ordering.application.dto.order;

import lombok.Builder;
import ordering.domain.model.DeliveryMode;
import shared.enums.PaymentMethod;
import ordering.domain.model.OrderStatus;

import java.util.List;

@Builder(toBuilder = true)
public record OrderRequestDTO(
        List<OrderItemDTO> items,
        ClientInfoDTO client,
        String restaurantId,
        String deliveryAddress,
        PaymentMethod paymentMethod,
        DeliveryMode deliveryMode,

        OrderStatus status,
        Double amountGivenByClient ,
        String promoCode
) {
}
