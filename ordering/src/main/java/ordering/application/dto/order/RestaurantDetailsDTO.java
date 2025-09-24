package ordering.application.dto.order;

public record RestaurantDetailsDTO(
        String restaurantId,
        String name,
        String phone,
        String address
) {
}
