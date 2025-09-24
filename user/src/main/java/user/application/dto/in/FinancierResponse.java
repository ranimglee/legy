package user.application.dto.in;

public record FinancierResponse(
        String id,
        String username,
        String firstname,
        String lastname,
        String email,
        String phoneNumber,
        String rib,
        Double longitude,
        Double latitude,
        String role

) {
}
