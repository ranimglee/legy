package ordering.application.dto.order;

public record CategorySalesDTO(
        String categoryId,
        String categoryName,
        int totalSold
) {}
