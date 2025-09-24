package delivery.domain.event.OrderEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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
    private Long deliveredAt;
    private Double distanceKm;
    private Double deliveryDurationMinutes ;

    private Boolean deliveredOnTime;
    private Long assignmentCount;




}

