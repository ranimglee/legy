package ordering.application.dto.order;

import ordering.domain.model.OrderItem;
import ordering.domain.model.OrderStatus;
import ordering.domain.model.value.DeliveryInfo;

import java.util.List;

public record OrderInfoDTO(
        String orderId,
        List<OrderItem> items,
        double total,
        OrderStatus orderStatus,
        String deliveryAddress,
        ClientInfoDTO client,
        RestaurantDetailsDTO restaurant,
        DeliveryInfo driver

) {}
