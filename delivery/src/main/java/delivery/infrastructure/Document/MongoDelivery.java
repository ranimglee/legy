package delivery.infrastructure.Document;

import delivery.domain.event.OrderEvent.ClientInfo;
import delivery.domain.event.OrderEvent.DeliveryInfo;
import delivery.domain.event.OrderEvent.OrderItem;
import delivery.domain.event.OrderEvent.RestaurantInfo;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "Delivery")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MongoDelivery  {
    @Id
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

