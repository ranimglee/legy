package user.application.dto.in;

import java.time.Instant;

public record ModerateurResponse(
        String username,
        String firstname,
        String lastname,
        String email,
        String phoneNumber,
        String rib,
        Instant createdAt
) {}
