package user.infrastructure.kafka.ClosestLivreur;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import user.application.service.LivreurFinderService;
import user.domain.event.ClosestLivreurRequest;
import user.domain.event.ClosestLivreursResponse; // Updated to handle the list
import user.domain.event.ClosestLivreurResponse;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClosestLivreurRequestConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ClosestLivreurRequestConsumer.class);
    private final ObjectMapper               objectMapper;
    private final LivreurFinderService       livreurFinderService;
    private final ClosestLivreurResponseProducer producer;

    @KafkaListener(topics = "livreur-request-topic", groupId = "my-group", containerFactory = "closestLivreurKafkaListenerContainerFactory")
    public void consume(ClosestLivreurRequest event) {
        logger.info("📥 Received request: lat={}, lng={}, corrId={}",
                event.latitude(), event.longitude(), event.correlationId());

        List<ClosestLivreurResponse> livreurs;
        try {
            livreurs = livreurFinderService.findClosestLivreurs(event);
        } catch (Exception lookupEx) {
            logger.warn("⚠️ No reachable livreurs for corrId {}: {}",
                    event.correlationId(), lookupEx.getMessage());
            livreurs = Collections.emptyList();
        }

        ClosestLivreursResponse response =
                new ClosestLivreursResponse(event.correlationId(), livreurs);
        producer.sendClosestLivreurs(response);
        logger.info("📤 Sent response corrId={} with {} livreurs",
                event.correlationId(), livreurs.size());
    }

}