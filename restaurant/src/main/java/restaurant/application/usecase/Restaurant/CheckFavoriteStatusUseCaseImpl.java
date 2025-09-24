package restaurant.application.usecase.Restaurant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Restaurant.FavoriteStatusDTO;
import restaurant.application.usecase.Restaurant.CheckFavoriteStatusUseCase;
import restaurant.domain.repository.FavoriteRepository;

@Service
@RequiredArgsConstructor
public class CheckFavoriteStatusUseCaseImpl implements CheckFavoriteStatusUseCase {

    private final FavoriteRepository favoriteRepository;

    @Override
    public FavoriteStatusDTO execute(String userId, String restaurantId) {
        boolean exists = favoriteRepository
                .findByUserIdAndRestaurantId(userId, restaurantId)
                .isPresent();
        return new FavoriteStatusDTO(exists);
    }
}
