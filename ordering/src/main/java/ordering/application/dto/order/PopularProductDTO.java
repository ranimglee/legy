package ordering.application.dto.order;

public record PopularProductDTO(
        String productId,
         String name,
         String imageUrl,
        int totalSold
) {
}
