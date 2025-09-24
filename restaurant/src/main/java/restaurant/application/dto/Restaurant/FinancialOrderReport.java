package restaurant.application.dto.Restaurant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restaurant.domain.event.Order.LivreurOrderStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinancialOrderReport {
    private String restaurantName;
    private String restaurantPhone;
    private double commission;
    private double revenueTotalCommission;
    private int nbrCommandesTotal;
    private String orderId;
    private LivreurOrderStatus livreurStatus;


}
