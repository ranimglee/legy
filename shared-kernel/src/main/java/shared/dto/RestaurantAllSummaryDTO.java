package shared.dto;

public record RestaurantAllSummaryDTO(
        String id,
        String name,
        String logo,
        double averageRating,
        long ratingCount
) {}
