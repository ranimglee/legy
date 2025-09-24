package recommendation.infrastructure.persistence.search;

import org.springframework.stereotype.Repository;
import recommendation.domain.model.SearchHistoryEntity;
import recommendation.domain.repository.SearchHistoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SearchHistoryMongoAdapter implements SearchHistoryRepository {

    private final SearchHistoryMongoRepository repository;

    public SearchHistoryMongoAdapter(SearchHistoryMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(SearchHistoryEntity searchHistory) {
        MongoSearchHistoryDocument document = MongoSearchHistoryDocument.builder()
                .id(searchHistory.getId())
                .clientId(searchHistory.getClientId())
                .keyword(searchHistory.getKeyword())
                .searchedAt(searchHistory.getSearchedAt())
                .build();
        repository.save(document);
    }

    @Override
    public List<SearchHistoryEntity> findByClientId(String clientId) {
        return repository.findByClientId(clientId)
                .stream()
                .map(doc -> new SearchHistoryEntity(doc.getId(), doc.getClientId(), doc.getKeyword(), doc.getSearchedAt()))
                .collect(Collectors.toList());
    }
}