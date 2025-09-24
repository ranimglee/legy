package user.application.dto.in;

import java.time.Instant;

public record ClientResponse(
        String username,
        String firstname,
        String lastname,
        String email,
        String phoneNumber,
        String address,
        Double longitude,
        Double latitude,
        Instant createdAt
) {}