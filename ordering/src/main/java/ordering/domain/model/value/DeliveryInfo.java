package ordering.domain.model.value;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    private Date acceptedAt;
    private Date etaDeadline;
    private int etaMinutes;


    public DeliveryInfo(String deliveryPersonId, String deliveryPersonName, String contactNumber, String vehicleInfo) {
        this.deliveryPersonId = deliveryPersonId;
        this.deliveryPersonName = deliveryPersonName;
        this.contactNumber = contactNumber;
        this.vehicleInfo = vehicleInfo;
    }
}
