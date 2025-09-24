package restaurant.application.dto.Product;

import lombok.NonNull;

/**
 * Holds just the average rating and count for a single product.
 */
public record ProductRatingDTO(
        @NonNull String productId,
        double averageRating,
        long ratingCount
) {
}
