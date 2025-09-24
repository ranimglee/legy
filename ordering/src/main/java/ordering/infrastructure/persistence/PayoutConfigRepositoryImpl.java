package ordering.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import ordering.domain.model.PayoutConfigEntity;
import ordering.domain.repository.PayoutConfigRepository;
import ordering.infrastructure.Document.PayoutConfigDocument;
import ordering.infrastructure.mapper.PayoutConfigMapper;
import ordering.infrastructure.repository.PayoutConfigRepositoryMongo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PayoutConfigRepositoryImpl implements PayoutConfigRepository {

    private final PayoutConfigRepositoryMongo payoutConfigRepositoryMongo;

    @Override
    public Optional<PayoutConfigEntity> findById(String id) {
        return payoutConfigRepositoryMongo.findById(id)
                .map(PayoutConfigMapper::toEntity);
    }

    @Override
    public PayoutConfigEntity save(PayoutConfigEntity entity) {
        PayoutConfigDocument document = PayoutConfigMapper.toDocument(entity);
        PayoutConfigDocument saved = payoutConfigRepositoryMongo.save(document);
        return PayoutConfigMapper.toEntity(saved);
    }

    @Override
    public boolean existsById(String id) {
        return payoutConfigRepositoryMongo.existsById(id); // Check if the document exists by ID in the MongoDB repository
    }

    @Override
    public void deleteById(String id) {
        // Delete the document by ID from the MongoDB repository
        payoutConfigRepositoryMongo.deleteById(id);
    }

    @Override
    public Iterable<PayoutConfigEntity> findAll() {
            List<PayoutConfigDocument> payoutConfigDocuments = payoutConfigRepositoryMongo.findAll();
            return payoutConfigDocuments.stream()
                    .map(PayoutConfigMapper::toEntity)
                    .collect(Collectors.toList());
    }

}
