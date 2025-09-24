package user.application.dto.in;


import io.swagger.v3.oas.annotations.media.Schema;

public record FirebaseLoginRequest(
        @Schema(description = "The Firebase ID token returned after Google Sign-In", example = "")
        String idToken
) {}
