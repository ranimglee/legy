package shared.events;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRecommendationProducer {

    private static final String USER_RECOMMENDATION_TOPIC = "recommendation_requests";

    private final KafkaTemplate<String, UserOrderRecommendationEvent> kafkaTemplate;

    public void sendUserRecommendationEvent(String userId) {
        try {
            var event = new UserOrderRecommendationEvent(userId);
            kafkaTemplate.send(USER_RECOMMENDATION_TOPIC, userId, event);
            System.out.println("✅ UPDATE USER_RECOMMENDATION_TOPIC sent " + event);

        } catch (Exception e) {
            System.err.println("❌ Failed to send update user recommendation event: " + e.getMessage());
        }
    }
}
