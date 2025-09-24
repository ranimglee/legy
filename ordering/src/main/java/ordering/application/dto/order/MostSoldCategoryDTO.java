package ordering.application.dto.order;

public record MostSoldCategoryDTO(
        String categoryId,
        String categoryName,
        int totalSold
) {
}
