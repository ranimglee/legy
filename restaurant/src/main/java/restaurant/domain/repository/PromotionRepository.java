package restaurant.domain.repository;

import org.springframework.stereotype.Repository;
import restaurant.domain.model.Promotion;


import java.util.List;
import java.util.Optional;
@Repository
public interface PromotionRepository {
    Promotion save(Promotion promotion);
    Optional<Promotion> findById(String id);
    void delete(Promotion promotion);
    List<Promotion> findAll();


    List<Promotion> findByRestaurantId(String restaurantId);
}
