package user.infrastructure.mapper;

import org.springframework.stereotype.Component;
import user.domain.model.Evaluation;
import user.infrastructure.persistence.entities.EvaluationDocument;

@Component
public class EvaluationMapper {
    public EvaluationDocument toDocument(Evaluation e) {
        return new EvaluationDocument(
                e.getId(),
                e.getLivreurId(),
                e.getClientId(),
                e.getRating(),
                e.getComment(),
                e.getCreatedAt()
        );
    }

    public Evaluation toDomain(EvaluationDocument d) {
        return Evaluation.builder()
                .id(d.getId())
                .livreurId(d.getLivreurId())
                .clientId(d.getClientId())
                .rating(d.getRating())
                .comment(d.getComment())
                .createdAt(d.getCreatedAt())
                .build();
    }
}
