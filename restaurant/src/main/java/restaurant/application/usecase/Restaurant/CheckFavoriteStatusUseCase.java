package restaurant.application.usecase.Restaurant;

import restaurant.application.dto.Restaurant.FavoriteStatusDTO;

public interface CheckFavoriteStatusUseCase {
    FavoriteStatusDTO execute(String userId, String restaurantId);
}
