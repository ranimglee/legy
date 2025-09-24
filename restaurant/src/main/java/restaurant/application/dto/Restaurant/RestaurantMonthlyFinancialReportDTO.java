package restaurant.application.dto.Restaurant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RestaurantMonthlyFinancialReportDTO {
    private String restaurantId;
    private String restaurantName;
    private int year;
    private int month;
    private int totalOrders;
    private double totalRevenue;
    private double totalCommissionEarned;
    private double averageOrderValue;
}