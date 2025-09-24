package user.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import user.domain.event.RestaurantCreationFailedEvent;
import user.domain.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestaurantCreationFailureListener {

    private final UserRepository userRepository;

    @KafkaListener(topics = "restaurant.creation.failed", groupId = "user-service")
    public void handle(RestaurantCreationFailedEvent event) {
        log.warn("🧹 Rolling back manager creation due to restaurant failure. Deleting managerId: {}", event.managerId());

        userRepository.deleteById(event.managerId());
    }
}