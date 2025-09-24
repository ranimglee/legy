package restaurant.application.usecase.Restaurant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Restaurant.UserReactionStatusDTO;
import restaurant.domain.repository.RestaurantReactionRepository;

@Service
@RequiredArgsConstructor
public class GetUserRestaurantReactionUseCase {
    private final RestaurantReactionRepository repository;

    public UserReactionStatusDTO execute(String restaurantId, String userId) {
        return repository.findByUserAndRestaurant(userId, restaurantId)
                .map(reaction -> UserReactionStatusDTO.of(reaction.getReaction().name()))
                .orElse(UserReactionStatusDTO.none());
    }
}

