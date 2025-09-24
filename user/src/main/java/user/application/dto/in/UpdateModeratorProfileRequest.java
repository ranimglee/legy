package user.application.dto.in;

public record UpdateModeratorProfileRequest(
        String firstName,
        String lastName,
        String username,
        String phoneNumber,
        String rib) {

}
