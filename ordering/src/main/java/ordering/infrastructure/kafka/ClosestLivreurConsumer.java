package ordering.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ordering.application.dto.order.ClosestLivreursResponse;

@Service
public class ClosestLivreurConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ClosestLivreurConsumer.class);
    private final ObjectMapper objectMapper;
    private final KafkaResponseAwaiter responseAwaiter;

    public ClosestLivreurConsumer(ObjectMapper objectMapper, KafkaResponseAwaiter responseAwaiter) {
        this.objectMapper = objectMapper;
        this.responseAwaiter = responseAwaiter;
    }

    @KafkaListener(topics = "closest-livreur-response", groupId = "restaurant-group", containerFactory = "restaurantKafkaListenerContainerFactory")
    public void consume(@Payload String message) {
        try {
            ClosestLivreursResponse response = objectMapper.readValue(message, ClosestLivreursResponse.class);
            logger.info("✅ Livreurs received for Correlation ID: {}", response.getCorrelationId());
            responseAwaiter.complete(response.getCorrelationId(), response);
        } catch (Exception e) {
            logger.error("Error deserializing message", e);
        }
    }

}