package recommendation.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import recommendation.domain.model.SearchHistoryEntity;
import recommendation.domain.repository.SearchHistoryRepository;


@Service
public class SearchHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(SearchHistoryService.class);
    private final SearchHistoryRepository repository;

    public SearchHistoryService(SearchHistoryRepository repository) {
        this.repository = repository;
    }

    public void persistSearch(String userId, String keyword) {
        // Log the input to the persistSearch method
        logger.info("Persisting search history: userId = {}, keyword = {}", userId, keyword);

        // Create a new SearchHistory entity
        SearchHistoryEntity entity = SearchHistoryEntity.builder()
                .clientId(userId)
                .keyword(keyword)
                .searchedAt(java.time.Instant.now())
                .build();

        // Log before saving to the database
        logger.info("Saving to database: {}", entity);

        // Save it in the database
        repository.save(entity);
    }
}
