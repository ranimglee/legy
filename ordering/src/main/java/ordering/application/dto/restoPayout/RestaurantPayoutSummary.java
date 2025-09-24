package ordering.application.dto.restoPayout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantPayoutSummary {
    private String restaurantId;
    private String restaurantName;
    private double totalCommission;
    private double totalRevenueToPay;
    private boolean isPaid; // true if platform already paid the restaurant
}
