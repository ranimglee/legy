package restaurant.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import restaurant.domain.model.PromotionPlatform;
import shared.enums.PromotionType;

import java.util.List;
import java.util.Optional;

public interface PromotionPlatformRepository {
    PromotionPlatform save(PromotionPlatform promotion);
    Optional<PromotionPlatform> findById(String promotionId);
    void delete(String id);
    List<PromotionPlatform> findAll();
    Page<PromotionPlatform> findAll(Pageable pageable);
    Page<PromotionPlatform> findAllByType(PromotionType type, Pageable pageable);

}
