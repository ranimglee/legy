package ordering.infrastructure.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ordering.application.dto.order.ClosestLivreurRequest;

@Slf4j
@Service
public class ClosestLivreurProducer {

    private final KafkaTemplate<String, ClosestLivreurRequest> kafkaTemplate;
    private static final String REQUEST_TOPIC = "livreur-request-topic";

    @Autowired
    public ClosestLivreurProducer(@Qualifier("restoKafkaTemplate") KafkaTemplate<String, ClosestLivreurRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRestaurantRequest(ClosestLivreurRequest event) {
        kafkaTemplate.send(REQUEST_TOPIC, event);
        log.info("===================> " + event.getLatitude() + " " + event.getLongitude());
    }
}
