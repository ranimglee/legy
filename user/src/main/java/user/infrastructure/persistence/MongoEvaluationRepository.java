package user.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import user.domain.model.Evaluation;
import user.domain.repository.EvaluationRepository;
import user.infrastructure.mapper.EvaluationMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MongoEvaluationRepository implements EvaluationRepository {
    private final SpringDataEvaluationRepo springRepo;
    private final EvaluationMapper mapper;

    @Override
    public Evaluation save(Evaluation eval) {
        var doc = mapper.toDocument(eval);
        var saved = springRepo.save(doc);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Evaluation> findById(String id) {
        return springRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Evaluation> findByLivreurId(String livreurId) {
        return springRepo.findByLivreurId(livreurId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public Optional<Evaluation> findByLivreurIdAndClientId(String l, String c) {
        return springRepo
                .findByLivreurIdAndClientId(l, c)
                .map(mapper::toDomain);
    }
}
