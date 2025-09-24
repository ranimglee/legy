package ordering.application.dto.order;


import lombok.AllArgsConstructor;
import lombok.Getter;
import ordering.domain.model.TrackingStatus;

@Getter
@AllArgsConstructor
public class OrderTrackingDTO {
    private final String orderId;
    private final TrackingStatus trackingStatus;
}
