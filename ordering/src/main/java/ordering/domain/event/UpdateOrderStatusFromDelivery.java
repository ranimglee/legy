package ordering.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ordering.domain.model.OrderStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateOrderStatusFromDelivery {
    private String orderId;
    private OrderStatus newStatus;

    private String deliverymanId;
    private String deliverymanFirstName;
    private String deliverymanLastName;
    private String deliverymanPhone;


}
