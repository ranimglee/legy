/*package restaurant.infrastructure.kafka.ClosestLivreur;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import restaurant.domain.event.ClosestLivreur.ClosestLivreurResponse;
import restaurant.domain.event.ClosestLivreur.ClosestLivreursResponse;

@Service
public class ClosestLivreurConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ClosestLivreurConsumer.class);
    private final ObjectMapper objectMapper;
    private final KafkaResponseAwaiter responseAwaiter;

    public ClosestLivreurConsumer(ObjectMapper objectMapper, KafkaResponseAwaiter responseAwaiter) {
        this.objectMapper = objectMapper;
        this.responseAwaiter = responseAwaiter;
    }

    @KafkaListener(topics = "closest-livreur-topic", groupId = "closest-livreur-group")
    public void consume(@Payload String message) {
        try {
            ClosestLivreursResponse response = objectMapper.readValue(message, ClosestLivreursResponse.class);
            logger.info("✅ Livreurs received for Correlation ID: {}", response.getCorrelationId());
            responseAwaiter.complete(response.getCorrelationId(), response);

        } catch (JsonProcessingException e) {
            logger.error("❌ Failed to deserialize ClosestLivreurResponse: {}", e.getMessage());
        }
    }
}*/
