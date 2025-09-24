package ordering.infrastructure.persistence;


import ordering.domain.model.RefusalHistory;
import ordering.domain.repository.RefusalHistoryRepository;
import ordering.infrastructure.Document.MongoRefusalHistory;
import ordering.infrastructure.mapper.RefusalMapper;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class RefusalHistoryRepositoryImpl implements RefusalHistoryRepository {
    private final SpringDataRefusalRepository repository;

    public RefusalHistoryRepositoryImpl(SpringDataRefusalRepository repository) {
        this.repository = repository;
    }

    @Override
    public RefusalHistory save(RefusalHistory refusal) {
        return RefusalMapper.toDomain(repository.save(RefusalMapper.toMongo(refusal)));
    }

    @Override
    public List<RefusalHistory> findByLivreurId(String livreurId) {
        return repository.findByLivreurId(livreurId).stream()
                .map(RefusalMapper::toDomain)
                .collect(Collectors.toList());
    }


}
interface SpringDataRefusalRepository extends MongoRepository<MongoRefusalHistory, String> {
    List<MongoRefusalHistory> findByLivreurId(String livreurId);
}
