package user.application.dto.out;

public record ResetPasswordRequest(String email, String code, String newPassword) {
}
