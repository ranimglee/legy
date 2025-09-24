package restaurant.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import restaurant.domain.model.Avis;

import java.util.List;
import java.util.Optional;

public interface AvisRepository {
    Avis save(Avis avis);

    List<Avis> findByRestaurantId(String restaurantId);

    Optional<Avis> findByRestaurantIdAndUserId(String restaurantId, String userId);
    Page<Avis> findByRestaurantId(String restaurantId, Pageable pageable);
}
