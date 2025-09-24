package restaurant.application.dto.Product;

import lombok.NonNull;

/**
 * Slim view of a favorited product.
 */
public record FavoriteProductDTO(
        @NonNull String id,
        @NonNull String name,
        String imageUrl,
        double price ,
        String restaurantId
) {
}
