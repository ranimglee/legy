package user.application.dto.in;

import java.time.Instant;

public record RestaurantManagerResponse(
        String id,
        String username,
        String firstname,
        String lastname,
        String email,
        String phoneNumber,
        String rib,
        String role,
        Instant createdAt
) {}
