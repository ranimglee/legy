package user.application.dto.in;

public record EvaluateLivreurResponse(
        String id,
        String livreurId,
        String clientId,
        int rating,
        String comment,
        String createdAt
) {
}
