package restaurant.domain.event.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class UpdateOrderStatusFromDelivery {

    private String orderId;
    private OrderStatus newStatus;

    // Ajout des infos du livreur
    private String deliverymanId;
    private String deliverymanFirstName;
    private String deliverymanLastName;
    private String deliverymanPhone;
}
