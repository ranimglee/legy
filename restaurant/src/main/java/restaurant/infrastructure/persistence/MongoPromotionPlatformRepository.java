package restaurant.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import restaurant.infrastructure.Document.MongoPromotionPlatform;
import shared.enums.PromotionType;

import java.util.Collection;

public interface MongoPromotionPlatformRepository extends MongoRepository<MongoPromotionPlatform,String> {

    Page<MongoPromotionPlatform> findAll(Pageable pageable);
    Page<MongoPromotionPlatform> findByType(PromotionType type, Pageable pageable);
    Page<MongoPromotionPlatform> findByActive(boolean active, Pageable pageable);
    Page<MongoPromotionPlatform> findByTypeAndActive(PromotionType type, boolean active, Pageable pageable);

    Collection<Object> findByActiveTrue();
}
