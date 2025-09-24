package restaurant.application.dto.Product;

import lombok.NonNull;

public record ProductSummaryDTO(
        @NonNull String id,
        @NonNull String name,
        @NonNull String imageUrl,
        double price,
        @NonNull String description

) {
}
