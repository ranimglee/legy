package shared.domain.event;

public record RestaurantManagerCreatedEvent(
        String managerId,
        String email,
        String rib,
        String username,
        String createdBy
) {}
