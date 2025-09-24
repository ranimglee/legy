package ordering.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import ordering.domain.model.Payout;
import ordering.domain.repository.PayoutRepository;

import ordering.infrastructure.Document.PayoutDocument;
import ordering.infrastructure.mapper.PayoutMapper;
import ordering.infrastructure.repository.MongoPayoutRestoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class MongoPayoutRepositoryImpl implements PayoutRepository {

private final MongoPayoutRestoRepository mongoPayoutRepository;
    @Override
    public void save(Payout payout) {
        PayoutDocument document = PayoutMapper.toDocument(payout);
        mongoPayoutRepository.save(document);
    }

    @Override
    public List<Payout> findAllByRestaurantIdOrderByPayoutDateDesc(String restaurantId) {
        return mongoPayoutRepository
                .findAllByRestaurantIdOrderByPayoutDateDesc(restaurantId)
                .stream()
                .map(PayoutMapper::toDomain)
                .toList();
    }


    @Override
    public List<Payout> findAll() {
        List<PayoutDocument> documents = mongoPayoutRepository.findAll();
        return documents.stream()
                .map(PayoutMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Payout> findById(String payoutId) {
        return mongoPayoutRepository.findById(payoutId)
                .map(PayoutMapper::toDomain);
    }

    @Override
    public Optional<Payout> findFirstByRestaurantIdOrderByPayoutDateDesc(String restaurantId) {
        return mongoPayoutRepository
                .findFirstByRestaurantIdOrderByPayoutDateDesc(restaurantId)
                .map(PayoutMapper::toDomain);
    }



}
