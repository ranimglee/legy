package restaurant.application.dto.Notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import restaurant.domain.event.Order.OrderPlacedEvent;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderNotification {
    private String orderId;
    private String message;
    private String status;

    private List<OrderPlacedEvent.OrderItem> items;
    private OrderPlacedEvent.ClientInfo client;
    private String restaurantId;
}
