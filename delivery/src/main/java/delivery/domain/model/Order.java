// delivery.domain.model.Order
package delivery.domain.model;

import delivery.domain.event.OrderEvent.ClientInfo;
import delivery.domain.event.OrderEvent.DeliveryInfo;
import delivery.domain.event.OrderEvent.OrderItem;
import delivery.domain.event.OrderEvent.RestaurantInfo;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class  Order  {
    private String id;
    private ClientInfo client;
    private RestaurantInfo restaurant;
    private DeliveryInfo deliveryInfo;
    private List<OrderItem> items;
    private double total;
    private Date deliveredAt;
    private Double distanceKm;
    private Double deliveryDurationMinutes ;
    private Boolean deliveredOnTime;
    private Long assignmentCount;


}
