package shared.dto;

public record ProductAllSummaryDTO(
        String id,
        String name,
        String imageUrl,
        double price,
        double averageRating,
        long ratingCount
) {}
