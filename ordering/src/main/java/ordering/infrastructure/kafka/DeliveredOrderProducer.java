package ordering.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ordering.domain.event.OrderDeliveredEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@Service
public class DeliveredOrderProducer {

    private final KafkaTemplate<String, OrderDeliveredEvent> kafkaTemplate;

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    public void sendDeliveredEvent(OrderDeliveredEvent event) {
        kafkaTemplate.send("order.delivered", event.getOrderId(), event);

        log.info("📤 Kafka event sent for delivered order:");
        log.info("🆔 Order ID: {}", event.getOrderId());
        log.info("💰 Total: {}", event.getTotal());
        log.info("👤 Client: {}", event.getClient());
        log.info("🍽️ Restaurant: {}", event.getRestaurant());
        log.info("🚚 Delivery Info: {}", event.getDeliveryInfo());
        log.info("📦 Items: {}", event.getItems());
        log.info("⏰ Delivered At: {}", formatter.format(Instant.ofEpochMilli(event.getDeliveredAt().getTime())));
        log.info("📦 Distance: {}", event.getDistanceKm());


    }
}
