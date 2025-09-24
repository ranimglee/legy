package recommendation.infrastructure.persistence.search;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchHistoryMongoRepository extends MongoRepository<MongoSearchHistoryDocument, String> {
    List<MongoSearchHistoryDocument> findByClientId(String clientId);
}