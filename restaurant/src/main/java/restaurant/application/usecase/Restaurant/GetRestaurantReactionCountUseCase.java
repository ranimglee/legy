package restaurant.application.usecase.Restaurant;

import org.springframework.stereotype.Service;
import restaurant.application.dto.Restaurant.ReactionResponseDTO;
import restaurant.domain.model.RestaurantReaction;
import restaurant.domain.repository.RestaurantReactionRepository;

@Service
public class GetRestaurantReactionCountUseCase {

    private final RestaurantReactionRepository repository;

    public GetRestaurantReactionCountUseCase(RestaurantReactionRepository repository) {
        this.repository = repository;
    }

    public ReactionResponseDTO execute(String restaurantId) {
        long likes = repository.countByRestaurantAndReaction(restaurantId, RestaurantReaction.ReactionType.LIKE);
        long dislikes = repository.countByRestaurantAndReaction(restaurantId, RestaurantReaction.ReactionType.DISLIKE);
        return new ReactionResponseDTO(likes, dislikes);
    }
}
