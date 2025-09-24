package user.domain.repository;

import user.domain.model.Evaluation;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository {
    Evaluation save(Evaluation eval);

    Optional<Evaluation> findById(String id);

    List<Evaluation> findByLivreurId(String livreurId);

    Optional<Evaluation> findByLivreurIdAndClientId(String livreurId, String clientId);

}
