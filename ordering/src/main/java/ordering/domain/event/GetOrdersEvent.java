package ordering.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ordering.domain.model.Order;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class GetOrdersEvent {

    private String restaurantId;
    private List<Order> orders;


}
