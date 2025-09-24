package user.domain.event;

public record RestaurantCreationFailedEvent(String managerId, String reason) {}
