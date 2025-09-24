package shared.dto;


public record RestaurantInfoDTO(
        String restaurantId,
        String name,
        String phone,
        String address,
        Double commission,
        Double longitude,
        Double latitude,
        String logo,
        Double totalRevenueCommission,
        Integer nbrCommandesTotal
) {
    public RestaurantInfoDTO(String id, String nom, String telephone, String adresse, Double commission,
                             Double latitude, Double longitude, String logo) {
        this(id, nom, telephone, adresse, commission, longitude, latitude, logo, null, null);
    }
}

