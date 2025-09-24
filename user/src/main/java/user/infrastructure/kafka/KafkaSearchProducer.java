package user.infrastructure.kafka;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import user.domain.event.SearchEvent;

@Service
public class KafkaSearchProducer {

    private final KafkaTemplate<String, SearchEvent> kafkaTemplate;

    public KafkaSearchProducer(@Qualifier("searchEventKafkaTemplate") KafkaTemplate<String, SearchEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendSearchEvent(String userId, String keyword) {
        System.out.println("Sending search event to Kafka. User ID: " + userId + ", Keyword: " + keyword);

        SearchEvent event = new SearchEvent(userId, keyword);
        kafkaTemplate.send("search-topic", event);

    }
}
