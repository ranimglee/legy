package restaurant.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import restaurant.infrastructure.Document.MongoProductEvaluation;

import java.util.List;
import java.util.Optional;

public interface SpringDataProductEvalRepo
        extends MongoRepository<MongoProductEvaluation, String> {
    List<MongoProductEvaluation> findByProductId(String productId);

    Optional<MongoProductEvaluation> findByProductIdAndClientId(String productId, String clientId);
}
