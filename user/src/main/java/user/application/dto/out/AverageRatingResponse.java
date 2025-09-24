package user.application.dto.out;

public record AverageRatingResponse(
        String livreurId,
        double averageRating
) {
}
