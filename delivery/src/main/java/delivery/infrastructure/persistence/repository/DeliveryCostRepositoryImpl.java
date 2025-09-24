package delivery.infrastructure.persistence.repository;

import delivery.domain.model.DeliveryCost;
import delivery.domain.repository.DeliveryCostRepository;
import delivery.infrastructure.persistence.document.DeliveryCostDocument;
import delivery.infrastructure.persistence.mapper.DeliveryCostMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DeliveryCostRepositoryImpl implements DeliveryCostRepository {
    private final DeliveryCostMongoRepository mongoRepo;

    public DeliveryCostRepositoryImpl(DeliveryCostMongoRepository mongoRepo) {
        this.mongoRepo = mongoRepo;
    }

    @Override
    public DeliveryCost save(DeliveryCost cost) {
        DeliveryCostDocument document = DeliveryCostMapper.toDocument(cost);
        DeliveryCostDocument saved = mongoRepo.save(document);
        return DeliveryCostMapper.toDomain(saved);
    }

    @Override
    public Optional<DeliveryCost> findByOrderId(String orderId) {
        return mongoRepo.findByOrderId(orderId)
                .map(DeliveryCostMapper::toDomain);
    }
}