package restaurant.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import restaurant.domain.model.Favorite;

import java.util.Optional;

public interface FavoriteRepository {
    Favorite save(Favorite favorite);

    Optional<Favorite> findByUserIdAndRestaurantId(String userId, String restaurantId);

    Page<Favorite> findByUserId(String userId, Pageable pageable);

    void deleteByUserIdAndRestaurantId(String userId, String restaurantId);
}
