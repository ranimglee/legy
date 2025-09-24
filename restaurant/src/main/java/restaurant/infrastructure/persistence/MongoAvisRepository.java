package restaurant.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import restaurant.infrastructure.Document.MongoAvis;

import java.util.List;
import java.util.Optional;

public interface MongoAvisRepository
        extends MongoRepository<MongoAvis, String> {
    List<MongoAvis> findAllByRestaurantId(String restaurantId);

    Optional<MongoAvis> findByRestaurantIdAndUserId(String restaurantId,
                                                    String userId);
    Page<MongoAvis> findByRestaurantId(String restaurantId, Pageable pageable);
}
