package ordering.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ordering.domain.model.OrderItem;
import ordering.domain.model.value.ClientInfo;
import ordering.domain.model.value.DeliveryInfo;
import ordering.domain.model.value.RestaurantInfo;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDeliveredEvent {
    private String orderId;
    private Double total;
    private List<OrderItem> items;
    private ClientInfo client;
    private RestaurantInfo restaurant;
    private DeliveryInfo deliveryInfo;
    private Date deliveredAt;
    private Double distanceKm;
    private Double deliveryDurationMinutes ;
    private Boolean deliveredOnTime;
    private Long assignmentCount;


}
