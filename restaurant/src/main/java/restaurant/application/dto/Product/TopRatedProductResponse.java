package restaurant.application.dto.Product;

public record TopRatedProductResponse(
        String productId,
        double averageRating
) {
}
