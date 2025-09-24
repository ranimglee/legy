package user.application.dto.in;

public record LoginResponse(String token,String refreshToken, String role) {
}
