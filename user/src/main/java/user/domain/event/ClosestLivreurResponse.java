package user.domain.event;

public record ClosestLivreurResponse(
        String livreurId,
        String username,
        String firstname,
        String lastname,
        String email,
        String phoneNumber,
        String rib,
        String matricule,
        double latitude,
        double longitude,
        double distanceKm,
        String correlationId
) {

}
