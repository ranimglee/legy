package restaurant.infrastructure.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import restaurant.domain.model.Supplement;
import restaurant.domain.repository.SupplementRepository;
import restaurant.infrastructure.Document.MongoSupplement;
import restaurant.infrastructure.mapper.SupplementMapper;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SupplementRepositoryImpl implements SupplementRepository {

    private final SpringDataSupplementRepository repository;

    @Autowired
    public SupplementRepositoryImpl(SpringDataSupplementRepository repository) {
        this.repository = repository;
    }

    @Override
    public Supplement save(Supplement supplement) {
        MongoSupplement mongoSupplement = SupplementMapper.toMongo(supplement);
        MongoSupplement saved = repository.save(mongoSupplement);
        return SupplementMapper.toDomain(saved);
    }

    @Override
    public Optional<Supplement> findById(String id) {
        return repository.findById(id)
                .map(SupplementMapper::toDomain);
    }

    @Override
    public List<Supplement> findAll() {
        return repository.findAll().stream()
                .map(SupplementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<Supplement> findAllById(List<String> ids) {
        return repository.findAllById(ids).stream()
                .map(SupplementMapper::toDomain)
                .collect(Collectors.toList());
    }
    @Override
    public List<Supplement> searchByNamePrefix(String query, int page, int size) {
        String anchored = "^" + query;
        var pageable    = PageRequest.of(page, size);
        return repository.findByNamePrefix(anchored, pageable)
                .stream()
                .map(SupplementMapper::toDomain)
                .collect(Collectors.toList());
    }

}
interface SpringDataSupplementRepository extends MongoRepository<MongoSupplement, String> {
    List<MongoSupplement> findAllById(Iterable<String> ids);

    @Query(
            value = "{ 'name': { $regex : ?0, $options: 'i' } }",
            fields = "{ '_id': 1, 'name': 1, 'price': 1, 'description': 1, 'createdby': 1, 'restaurantId': 1 }"
    )
    List<MongoSupplement> findByNamePrefix(String anchoredRegex, Pageable pageable);
}