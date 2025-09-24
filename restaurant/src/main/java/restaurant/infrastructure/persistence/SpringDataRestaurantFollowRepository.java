package restaurant.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import restaurant.domain.model.RestaurantFollow;
import restaurant.infrastructure.Document.MongoRestaurantFollow;

import java.util.List;

@Repository
public interface SpringDataRestaurantFollowRepository extends MongoRepository<MongoRestaurantFollow, String> {
    boolean existsByClientIdAndRestaurantId(String clientId, String restaurantId);
    void deleteByClientIdAndRestaurantId(String clientId, String restaurantId);

    @Query(value = "{ 'clientId': ?0 }", fields = "{ 'restaurantId': 1 }")
    List<RestaurantFollow> findByClientId(String clientId);

    @Query(value = "{ 'restaurantId': ?0 }", fields = "{ 'clientId': 1 }")
    List<RestaurantFollow> findByRestaurantId(String restaurantId);
    long countByRestaurantId(String restaurantId);


}
