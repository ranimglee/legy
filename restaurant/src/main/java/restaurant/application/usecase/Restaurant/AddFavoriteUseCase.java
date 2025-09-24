package restaurant.application.usecase.Restaurant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.exception.DuplicateFavoriteException;
import restaurant.application.exception.RestaurantNotFoundException;
import restaurant.domain.model.Favorite;
import restaurant.domain.repository.FavoriteRepository;
import restaurant.domain.service.RestaurantDomainService;

@Service
@RequiredArgsConstructor
public class AddFavoriteUseCase {

    private final FavoriteRepository favRepo;
    private final RestaurantDomainService restaurantService;

    public void execute(String userId, String restaurantId) {
        // ensure restaurant exists (throws RestaurantNotFoundException if not)
        restaurantService.getRestaurantById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        // avoid duplicates — throw DuplicateFavoriteException now
        favRepo.findByUserIdAndRestaurantId(userId, restaurantId)
                .ifPresent(f -> {
                    throw new DuplicateFavoriteException(userId, restaurantId);
                });

        Favorite fav = new Favorite();
        fav.setUserId(userId);
        fav.setRestaurantId(restaurantId);
        favRepo.save(fav);
    }
}
