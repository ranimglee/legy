package recommendation.domain.repository;

import recommendation.domain.model.SearchHistoryEntity;

import java.util.List;

public interface SearchHistoryRepository {
    void save(SearchHistoryEntity searchHistory);

    List<SearchHistoryEntity> findByClientId(String clientId);
}