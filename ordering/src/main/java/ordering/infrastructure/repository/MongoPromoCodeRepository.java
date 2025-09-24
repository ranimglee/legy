package ordering.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import ordering.domain.model.PromoCode;
import ordering.domain.repository.PromoCodeRepository;
import ordering.infrastructure.mapper.PromoCodeMapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MongoPromoCodeRepository implements PromoCodeRepository {

    private final SpringDataPromoCodeRepository repo;

    @Override
    public Optional<PromoCode> findByCode(String code) {
        return repo.findByCode(code).map(PromoCodeMapper::toDomain);
    }

    @Override
    public void save(PromoCode promoCode) {
        repo.save(PromoCodeMapper.toDocument(promoCode));
    }

    @Override
    public void incrementUsage(String code) {
        repo.findByCode(code).ifPresent(doc -> {
            doc.setCurrentUsage(doc.getCurrentUsage() + 1);
            repo.save(doc);
        });
    }

    @Override
    public List<PromoCode> findAll() {
        return repo.findAll().stream()
                .map(PromoCodeMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        repo.deleteById(id);
    }
    @Override
    public Optional<PromoCode> findById(String id) {
        return repo.findById(id).map(PromoCodeMapper::toDomain);
    }

}
