package restaurant.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import restaurant.infrastructure.Document.MongoFavorite;

import java.util.Optional;

interface SpringDataFavoriteRepository
        extends MongoRepository<MongoFavorite, String> {

    Optional<MongoFavorite> findByUserIdAndRestaurantId(
            String userId, String restaurantId);

    /**
     * NEW: paginated lookup
     */
    Page<MongoFavorite> findByUserId(String userId, Pageable pageable);

    void deleteByUserIdAndRestaurantId(String userId, String restaurantId);
}
