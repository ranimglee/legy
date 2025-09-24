package user.application.dto.out;

public record VerifyResetCodeRequest(String email, String code) {}