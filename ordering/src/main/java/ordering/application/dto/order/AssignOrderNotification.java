package ordering.application.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssignOrderNotification {
    private String orderId;
    private String restaurantId;
    private String livreurId;
    private double latitude;
    private double longitude;
    private long startTimeMillis;
    private String restaurantAddress;
    private String restaurantPhone;
}
