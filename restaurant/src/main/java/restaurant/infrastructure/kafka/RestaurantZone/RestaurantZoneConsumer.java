package restaurant.infrastructure.kafka.RestaurantZone;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import restaurant.domain.model.Restaurant;
import restaurant.domain.repository.RestaurantRepository;
import shared.domain.event.RestaurantAssignedToZoneEvent;
import shared.domain.event.RestaurantRemovedFromZoneEvent;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestaurantZoneConsumer {

    private final RestaurantRepository restaurantRepository;

    @KafkaListener(
            topics = "restaurant.assigned.zone",
            groupId = "restaurant-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void handleRestaurantAssigned(RestaurantAssignedToZoneEvent event) {
        log.info("📥 Received RestaurantAssignedToZoneEvent (assign): {}", event);

        if (event.getRestaurantId() == null) {
            log.error("❌ Event missing restaurantId — skipping.");
            return;
        }

        Optional<Restaurant> optional = restaurantRepository.findById(event.getRestaurantId());
        if (optional.isEmpty()) {
            log.warn("❌ Restaurant [{}] not found, skipping event.", event.getRestaurantId());
            return;
        }

        Restaurant restaurant = optional.get();
        restaurant.setIsAssigned(true);
        restaurant.setAssignedZoneId(event.getZoneId());

        restaurantRepository.save(restaurant);

        log.info("✅ Updated restaurant [{}] as assigned to zone [{}]", event.getRestaurantId(), event.getZoneId());
    }

    @KafkaListener(
            topics = "restaurant.removed.zone",
            groupId = "restaurant-group",
            containerFactory = "restaurantRemovedKafkaListenerContainerFactory"
    )
    @Transactional
    public void handleRestaurantRemoved(RestaurantRemovedFromZoneEvent event){

        log.info("📥 Received RestaurantAssignedToZoneEvent (remove): {}", event);

        if (event.getRestaurantId() == null) {
            log.error("❌ Event missing restaurantId — skipping.");
            return;
        }

        Restaurant restaurant = restaurantRepository.findById(event.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        restaurant.setIsAssigned(false);
        restaurant.setAssignedZoneId(null);

        restaurantRepository.save(restaurant);

        log.info("✅ Updated restaurant [{}]: cleared assigned zone (was [{}])", event.getRestaurantId(), event.getZoneId());
    }
}
