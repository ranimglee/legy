package ordering.infrastructure.persistence;

import ordering.domain.model.HistoriqueRefus;
import ordering.domain.repository.HistoriqueRefusRepository;
import ordering.infrastructure.mapper.HistoriqueRefusMapper;
import ordering.infrastructure.Document.MongoHistoriqueRefus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class HistoriqueRefusRepositoryImpl implements HistoriqueRefusRepository {

    private final SpringDataHistoriqueRefusRepository repository;

    @Autowired
    public HistoriqueRefusRepositoryImpl(SpringDataHistoriqueRefusRepository repository) {
        this.repository = repository;
    }

    @Override
    public HistoriqueRefus save(HistoriqueRefus historiqueRefus) {
        // Convert domain model to Mongo model before saving
        MongoHistoriqueRefus mongoHistoriqueRefus = HistoriqueRefusMapper.toMongo(historiqueRefus);
        MongoHistoriqueRefus savedMongoHistoriqueRefus = repository.save(mongoHistoriqueRefus);
        // Convert Mongo model back to domain model
        return HistoriqueRefusMapper.toDomain(savedMongoHistoriqueRefus);
    }

    @Override
    public Optional<HistoriqueRefus> findById(String id) {
        // Retrieve MongoHistoriqueRefus by ID and convert to domain model
        Optional<MongoHistoriqueRefus> mongoHistoriqueRefus = repository.findById(id);
        return mongoHistoriqueRefus.map(HistoriqueRefusMapper::toDomain);
    }

    @Override
    public List<HistoriqueRefus> findAll() {
        // Retrieve all MongoHistoriqueRefus and convert them to domain models
        List<MongoHistoriqueRefus> mongoHistoriqueRefusList = repository.findAll();
        return mongoHistoriqueRefusList.stream()
                .map(HistoriqueRefusMapper::toDomain)
                .collect(Collectors.toList());
    }
}

// Spring Data repository interface for MongoDB
interface SpringDataHistoriqueRefusRepository extends MongoRepository<MongoHistoriqueRefus, String> {
}
