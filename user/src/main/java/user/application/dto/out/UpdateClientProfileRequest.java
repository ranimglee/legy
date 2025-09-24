package user.application.dto.out;

public record UpdateClientProfileRequest(
        String firstname,
        String lastname,
        String phoneNumber,
        String address

) {
}
