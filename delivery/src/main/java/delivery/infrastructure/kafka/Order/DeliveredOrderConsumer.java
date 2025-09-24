package delivery.infrastructure.kafka.Order;

import com.fasterxml.jackson.databind.ObjectMapper;
import delivery.domain.event.OrderEvent.OrderDeliveredEvent;
import delivery.domain.model.Order;
import delivery.domain.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveredOrderConsumer {

    private final ObjectMapper objectMapper;
    private final DeliveryRepository orderRepository;

    @KafkaListener(
            topics = "order.delivered",
            groupId = "order-delivered-group",
            containerFactory = "orderDeliveredKafkaListenerFactory"
    )
    public void consume(ConsumerRecord<String, String> record) {
        try {
            String json = record.value();
            OrderDeliveredEvent event = objectMapper.readValue(json, OrderDeliveredEvent.class);

            log.info("📥 Received delivered order event: {}", event);
            log.info("📬 Reçu OrderDeliveredEvent avec distanceKm: {}", event.getDistanceKm());

            Order order = Order.builder()
                    .id(event.getOrderId())
                    .client(event.getClient())
                    .restaurant(event.getRestaurant())
                    .deliveryInfo(event.getDeliveryInfo())
                    .items(event.getItems())
                    .total(event.getTotal())
                    .deliveredAt(new Date(event.getDeliveredAt()))
                    .distanceKm(event.getDistanceKm())
                    .deliveryDurationMinutes(event.getDeliveryDurationMinutes())
                    .deliveredOnTime(event.getDeliveredOnTime())
                    .assignmentCount(event.getAssignmentCount())
                    .build();

            orderRepository.save(order);
            log.info("💾 Order saved to MongoDB with ID: {}", order.getId());
        } catch (Exception e) {
            log.error("❌ Failed to process delivered order event", e);
        }
    }


}
