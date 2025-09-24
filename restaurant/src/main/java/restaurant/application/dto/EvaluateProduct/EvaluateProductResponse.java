package restaurant.application.dto.EvaluateProduct;

public record EvaluateProductResponse(
        String productId,
        String clientId,
        int rating,
        String comment,
        String createdAt
) {
}