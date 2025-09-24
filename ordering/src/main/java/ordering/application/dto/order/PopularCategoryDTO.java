package ordering.application.dto.order;

public record PopularCategoryDTO(
        String categoryId,
        int totalSold
) {
}
