package shared.port;

public interface RestaurantUpdatePort {
    void incrementRestaurantOrderCountAndRevenue(String restaurantId, double commissionRevenue);

}