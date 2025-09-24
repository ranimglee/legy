package restaurant.application.dto.Product;

import shared.enums.AvailabilityStatus;

public record GuestProductResponseDTO(
        String name,
        String description,
        double price,
        String imageUrl,
        AvailabilityStatus availability

) {
}
