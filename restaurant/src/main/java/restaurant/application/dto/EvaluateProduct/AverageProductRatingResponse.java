package restaurant.application.dto.EvaluateProduct;

public record AverageProductRatingResponse(
        String productId,
        double averageRating
) {
}