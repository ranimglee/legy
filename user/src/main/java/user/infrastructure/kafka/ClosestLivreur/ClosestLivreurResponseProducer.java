package user.infrastructure.kafka.ClosestLivreur;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import user.domain.event.ClosestLivreursResponse;

@Service
public class ClosestLivreurResponseProducer {

    private static final Logger logger = LoggerFactory.getLogger(ClosestLivreurResponseProducer.class);
    private final KafkaTemplate<String, ClosestLivreursResponse> kafkaTemplate;

    public ClosestLivreurResponseProducer(@Qualifier("livreurKafkaTemplate") KafkaTemplate<String, ClosestLivreursResponse> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Update to send a list of livreurs
    public void sendClosestLivreurs(ClosestLivreursResponse response) {
        logger.info("📤 Sending sorted list of livreurs with correlationId = {}", response.correlationId());
        kafkaTemplate.send("closest-livreur-response", response);
    }
}
