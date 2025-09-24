package restaurant.application.usecase.Restaurant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.exception.FavoriteNotFoundException;
import restaurant.domain.repository.FavoriteRepository;

@Service
@RequiredArgsConstructor
public class RemoveFavoriteUseCase {

    private final FavoriteRepository favRepo;

    public void execute(String userId, String restaurantId) {
        favRepo.findByUserIdAndRestaurantId(userId, restaurantId)
                .orElseThrow(() -> new FavoriteNotFoundException(userId, restaurantId));
        favRepo.deleteByUserIdAndRestaurantId(userId, restaurantId);
    }
}
