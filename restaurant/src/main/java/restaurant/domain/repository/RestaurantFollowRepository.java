package restaurant.domain.repository;

import restaurant.domain.model.RestaurantFollow;

import java.util.List;

public interface RestaurantFollowRepository {
    RestaurantFollow save(RestaurantFollow follow);
    boolean existsByClientIdAndRestaurantId(String clientId, String restaurantId);
    void deleteByClientIdAndRestaurantId(String clientId, String restaurantId);

    List<String> findRestaurantIdsByClientId(String clientId);
    List<String> findClientIdsByRestaurantId(String restaurantId);
    long countByRestaurantId(String restaurantId);


}
