package user.application.dto.out;

public record ChangePasswordRequest(String currentPassword, String newPassword) {
}
