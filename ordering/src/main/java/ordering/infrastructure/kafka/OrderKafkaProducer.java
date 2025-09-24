package ordering.infrastructure.kafka;

import ordering.domain.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderKafkaProducer {

    private static final String ORDER_PLACED_TOPIC = "orderPlacedEvent";
    private static final Logger logger = LoggerFactory.getLogger(OrderKafkaProducer.class);

    private final KafkaTemplate<String, OrderPlacedEvent> orderKafkaTemplate;

    public OrderKafkaProducer(KafkaTemplate<String, OrderPlacedEvent> orderKafkaTemplate) {
        this.orderKafkaTemplate = orderKafkaTemplate;
    }

    public void sendOrderPlacedEvent(Order order) {
        try {
            List<OrderPlacedEvent.OrderItem> items = order.getItems().stream()
                    .map(item -> new OrderPlacedEvent.OrderItem(
                            item.getProductId(),
                            item.getQuantity(),
                            item.getProductName(),
                            item.getUnitPrice()
                    ))
                    .toList();

            OrderPlacedEvent.ClientInfo clientInfo = new OrderPlacedEvent.ClientInfo(
                    order.getClient().getClientId(),
                    order.getClient().getFirstName(),
                    order.getClient().getLastName(),
                    order.getClient().getPhone(),
                    order.getClient().getAddress()
            );

            OrderPlacedEvent event = new OrderPlacedEvent(
                    order.getId(),
                    items,
                    order.getRestaurant().getRestaurantId(),
                    clientInfo,
                    OrderPlacedEvent.OrderStatus.valueOf(order.getOrderStatus().name())
            );

            orderKafkaTemplate.send(ORDER_PLACED_TOPIC, order.getId(), event);
            System.out.println("✅ OrderPlaced event sent: " + event);
        } catch (Exception e) {
            logger.error("Kafka send failed for orderPlacedEvent, orderId={}", order.getId(), e);
        }
    }
}
