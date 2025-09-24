package delivery.domain.event.OrderEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInfo {

    private String deliveryPersonId;
    private String deliveryPersonName;
    private String contactNumber;
    private String vehicleInfo;
    private String estimatedArrivalTime;




}
