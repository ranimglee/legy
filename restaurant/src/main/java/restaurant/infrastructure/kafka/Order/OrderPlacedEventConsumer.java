package restaurant.infrastructure.kafka.Order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Notification.OrderNotification;
import restaurant.domain.event.Order.OrderPlacedEvent;
import restaurant.domain.event.Order.OrderStatus;

import java.time.Duration;

@Service
public class OrderPlacedEventConsumer {

    private OrderStatusProducer orderStatusProducer;
    @Qualifier("saveRestoNotifRedisTemplate")
    private final RedisTemplate redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(OrderPlacedEventConsumer.class);
    private final SimpMessagingTemplate messagingTemplate;

    public OrderPlacedEventConsumer(    @Qualifier("saveRestoNotifRedisTemplate")
                                        RedisTemplate redisTemplate, SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(
            topics = "orderPlacedEvent",
            groupId = "order-group",
            containerFactory = "orderPlacedKafkaListenerContainerFactory"
    )
    public void consumeOrderPlacedEvent(OrderPlacedEvent event) {
        logger.info("✅ Order received: {}", event.getOrderId());

        // Construct notification with full info
        OrderNotification notification = new OrderNotification(
                event.getOrderId(),
                "New order received!",
                event.getOrderStatus().name(),
                event.getItems(),
                event.getClient(),
                event.getRestaurantId()
        );

        String redisKey = "restaurant:notif:" + event.getRestaurantId() + ":" + event.getOrderId();
        redisTemplate.opsForValue().set(redisKey, notification, Duration.ofMinutes(1));

        // Send to restaurant topic
        messagingTemplate.convertAndSend(
                "/topic/restaurant/" + event.getRestaurantId(),
                notification
        );

        logger.info("🔔 Notification sent to restaurant {}: {}", event.getRestaurantId(), notification);
    }
}
