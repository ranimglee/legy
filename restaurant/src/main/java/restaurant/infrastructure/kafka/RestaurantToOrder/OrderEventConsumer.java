package restaurant.infrastructure.kafka.RestaurantToOrder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import restaurant.domain.event.Order.GetOrdersEvent;
import restaurant.domain.event.Order.OrderDTO;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OrderEventConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    private final List<GetOrdersEvent> receivedEvents = new ArrayList<>();

    @KafkaListener(
            topics = "orders-for-restaurant",
            groupId = "restaurant-module-group",
            containerFactory = "restaurantKafkaListenerContainerFactory"
    )
    public void consumeOrdersEvent(String payload) {
        try {
            // Parse the incoming JSON
            JsonNode root = objectMapper.readTree(payload);

            String restaurantId = root.get("restaurantId").asText();
            List<OrderDTO> orders = new ArrayList<>();

            for (JsonNode orderNode : root.get("orders")) {
                OrderDTO orderDTO = objectMapper.treeToValue(orderNode, OrderDTO.class);
                orders.add(orderDTO);
            }

            GetOrdersEvent event = new GetOrdersEvent();
            event.setRestaurantId(restaurantId);
            event.setOrders(orders);

            receivedEvents.add(event);

            log.info("✅ Received GetOrdersEvent for restaurantId={} with {} orders", restaurantId, orders.size());

        } catch (Exception e) {
            log.error("❌ Failed to deserialize GetOrdersEvent", e);
        }
    }

    public List<GetOrdersEvent> getReceivedEvents() {
        return receivedEvents;
    }
}
