package user.application.dto.in;

public record FirebaseLoginResponse(
        String token,
        String refreshToken,
        String role,
        boolean missingPhoneNumber
) {}
