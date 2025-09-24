package recommendation.infrastructure.persistence.search.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import recommendation.application.service.SearchHistoryService;
import recommendation.domain.event.SearchEvent;

@Service
public class KafkaSearchConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaSearchConsumer.class);
    private final SearchHistoryService searchHistoryService;
    private final ObjectMapper objectMapper;

    public KafkaSearchConsumer(SearchHistoryService searchHistoryService, ObjectMapper objectMapper) {
        this.searchHistoryService = searchHistoryService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "search-topic", groupId = "recommendation-group")
    public void consume(String message) {
        try {
            SearchEvent searchEvent = objectMapper.readValue(message, SearchEvent.class);

            logger.info("Received SearchEvent: userId = {}, keyword = {}", searchEvent.userId(), searchEvent.keyword());
            searchHistoryService.persistSearch(searchEvent.userId(), searchEvent.keyword());

        } catch (Exception e) {
            logger.error("Error deserializing SearchEvent: {}", e.getMessage());
        }
    }
}
