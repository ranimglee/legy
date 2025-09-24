package user.application.dto.out;

public record ForgotPasswordRequest(
        String email,
        String channel
) {
}
