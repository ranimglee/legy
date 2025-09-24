package ordering.domain.repository;

import ordering.domain.model.PromoCode;

import java.util.List;
import java.util.Optional;

public interface PromoCodeRepository {
    Optional<PromoCode> findByCode(String code);
    void save(PromoCode promoCode);
    void incrementUsage(String code);
    List<PromoCode> findAll();
    void deleteById(String id);
    Optional<PromoCode> findById(String id);

}
