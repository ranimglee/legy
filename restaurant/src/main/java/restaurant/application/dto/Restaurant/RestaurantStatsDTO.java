package restaurant.application.dto.Restaurant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class RestaurantStatsDTO {
    private long totalRestaurants;
    private long totalOrders;
    private double totalPlatformRevenue;
    private List<TopRestaurantDTO> topByOrders;
    private List<TopRestaurantDTO> topByRating;
}