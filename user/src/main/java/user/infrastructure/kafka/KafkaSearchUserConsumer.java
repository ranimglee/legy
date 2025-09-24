package user.infrastructure.kafka;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import user.domain.event.SearchEvent;

@Service
public class KafkaSearchUserConsumer {

    @KafkaListener(topics = "search-topic", groupId = "my-group",
            containerFactory = "searchEventKafkaListenerContainerFactory")
    public void listenSearchEvent(SearchEvent event) {
        System.out.println("✅ Consumed search event from Kafka: " + event);
    }
}
