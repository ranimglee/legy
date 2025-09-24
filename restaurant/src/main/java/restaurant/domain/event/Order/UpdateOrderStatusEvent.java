package restaurant.domain.event.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateOrderStatusEvent {
    private String orderId;
    private String restaurantId;
    private OrderStatus newStatus;




}
