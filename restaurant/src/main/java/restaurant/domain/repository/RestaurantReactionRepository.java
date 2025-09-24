package restaurant.domain.repository;

import restaurant.domain.model.RestaurantReaction;

import java.util.Optional;

public interface RestaurantReactionRepository {
    Optional<RestaurantReaction> findByUserAndRestaurant(String userId, String restaurantId);
    void save(RestaurantReaction reaction);
    void delete(RestaurantReaction reaction);
    long countByRestaurantAndReaction(String restaurantId, RestaurantReaction.ReactionType type);
}
