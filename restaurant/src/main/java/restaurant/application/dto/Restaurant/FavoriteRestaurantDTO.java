package restaurant.application.dto.Restaurant;

import lombok.NonNull;

/**
 * A slim view of a restaurant for the “favorites” list.
 */
public record FavoriteRestaurantDTO(
        @NonNull String id,
        @NonNull String nom,
        String logo,
        double averageRating
) {
}
