package user.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import user.infrastructure.persistence.entities.EvaluationDocument;

import java.util.List;
import java.util.Optional;

public interface SpringDataEvaluationRepo
        extends MongoRepository<EvaluationDocument, String> {


    List<EvaluationDocument> findByLivreurId(String livreurId);

    Optional<EvaluationDocument> findByLivreurIdAndClientId(String livreurId, String clientId);
}
