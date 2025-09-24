package restaurant.infrastructure.kafka.RestaurantManagerCreation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import restaurant.domain.event.Restaurant.RestaurantCreationFailedEvent;
import restaurant.domain.model.Restaurant;
import restaurant.domain.model.RestaurantStatus;
import restaurant.domain.service.RestaurantDomainService;
import shared.domain.event.RestaurantManagerCreatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantManagerCreatedListener {

    private final RestaurantDomainService restaurantDomainService;
    private final KafkaTemplate<String, RestaurantCreationFailedEvent> restaurantCreationFailedkafkaTemplate;


    @KafkaListener(
            topics = "restaurant-m-created",
            groupId = "restaurant-service",
            containerFactory = "restaurantManagerKafkaListenerFactory"
    )
    public void handle(RestaurantManagerCreatedEvent event) {
        log.info("📥 Received event for managerId: {}", event.managerId());

        try {
            // Only create the restaurant here
            Restaurant restaurant = new Restaurant();
            restaurant.setRib(event.rib());
            restaurant.setRestaurantStatus(RestaurantStatus.PENDING);
            restaurant.setCreatedby(event.createdBy());
            restaurant.setManagerId(event.managerId());

            // Add other necessary fields from the event to the restaurant
            restaurantDomainService.addRestaurant(restaurant);

            log.info("✅ Restaurant created for managerId: {}", event.managerId());

        } catch (Exception ex) {
            log.error("❌ Failed to create restaurant for managerId: {}. Reason: {}", event.managerId(), ex.getMessage());

            RestaurantCreationFailedEvent failedEvent = new RestaurantCreationFailedEvent(event.managerId(), ex.getMessage());
            restaurantCreationFailedkafkaTemplate.send("restaurant.creation.failed", failedEvent);
        }
    }

}
