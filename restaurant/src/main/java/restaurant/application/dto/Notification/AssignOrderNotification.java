package restaurant.application.dto.Notification;

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
}
