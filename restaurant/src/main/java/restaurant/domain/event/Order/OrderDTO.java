package restaurant.domain.event.Order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true) // Allows safe deserialization even if fields are missing
public class OrderDTO {
    private String id;
    private List<OrderItemDTO> items;
    private ClientInfoDTO client;
    private RestaurantInfoDTO restaurant;
    private String deliveryAddress;
    private LivreurOrderStatus livreurStatus;
    private OrderStatus orderstatus;
}
 /*enum OrderStatus {
     PENDING,
     ACCEPTED,
     PREPARING,
     PREPARED,
     REFUSED,
     CANCELLED,
     COMPLETED
}
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class OrderItemDTO {
    private String productId;
    private String productName;
    private Double unitPrice;
    private int quantity;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class ClientInfoDTO {
    private String clientId;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
}

