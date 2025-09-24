package ordering.application.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationUpdateDTO {
    private String orderId;
    private String livreurId;
    private double latitude;
    private double longitude;
    private long   timestamp;
}