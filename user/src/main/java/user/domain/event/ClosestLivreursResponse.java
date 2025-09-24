package user.domain.event;

import java.util.List;

public record ClosestLivreursResponse(
        String correlationId,
        List<ClosestLivreurResponse> livreurs
) {}
