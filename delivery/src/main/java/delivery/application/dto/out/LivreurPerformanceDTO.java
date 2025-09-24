package delivery.application.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LivreurPerformanceDTO {
    private String deliveryPersonId;
    private double averageDeliveryDurationMinutes;
    private int totalDeliveries;
    private double averageOnTimeDeliveryDurationMinutes;
    private int onTimeDeliveries;
    private double successRate;
    private long assignmentCount;

}
