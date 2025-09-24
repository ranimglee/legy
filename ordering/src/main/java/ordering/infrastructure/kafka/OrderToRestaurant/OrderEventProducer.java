package ordering.infrastructure.kafka.OrderToRestaurant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ordering.domain.event.GetOrdersEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    @Qualifier("getOrdersKafkaTemplate")
    private final KafkaTemplate<String, GetOrdersEvent> kafkaTemplate;
    private static final String TOPIC = "orders-for-restaurant"; // define your topic name

    public void sendOrdersEvent(GetOrdersEvent event) {
        kafkaTemplate.send(TOPIC, event.getRestaurantId(), event);
        log.info("Sent GetOrdersEvent for restaurantId={} with {} orders to topic '{}'.",
                event.getRestaurantId(),
                event.getOrders() != null ? event.getOrders().size() : 0,
                TOPIC);
    }


}