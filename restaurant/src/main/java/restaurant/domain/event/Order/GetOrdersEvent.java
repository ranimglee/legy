package restaurant.domain.event.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetOrdersEvent {

    private String restaurantId;
    private List<OrderDTO> orders;
}
