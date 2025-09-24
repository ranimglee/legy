package restaurant.infrastructure.kafka.Order;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import restaurant.domain.event.Order.UpdateOrderStatusEvent;
import restaurant.domain.event.Order.OrderStatus;

@Service
@RequiredArgsConstructor
public class OrderStatusProducer {

    private final KafkaTemplate<String, UpdateOrderStatusEvent> updateOrderStatusKafkaTemplate;

    public void sendUpdateStatus(String orderId, String restaurantId,OrderStatus status) {
        UpdateOrderStatusEvent event = new UpdateOrderStatusEvent(orderId,restaurantId, status);
        updateOrderStatusKafkaTemplate.send("updateOrderStatus", event);
        System.out.println("✅ Sent updateOrderStatus event: " + event.getOrderId()+event.getNewStatus()+event.getRestaurantId());
    }
}
