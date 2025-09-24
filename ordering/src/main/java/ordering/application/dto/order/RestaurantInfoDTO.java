package ordering.application.dto.order;

public record RestaurantInfoDTO(
        String restaurantId,
        String name,
        String phone,
        String address,
        String city,
        double longitude,
        double latitude,
        Double totalRevenueCommission,
        Integer nbrCommandesTotal,
        String logo
) {

}

