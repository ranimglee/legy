package ordering.application.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ordering.domain.model.LivreurOrderStatus;
import ordering.domain.model.OrderStatus;

import java.util.List;

@Builder

public record UpdateOrderRequestDTO(
        @NotNull
        String orderId,
        List<OrderItemDTO> items,
        OrderStatus status,
        Boolean isPrepaid ,
        LivreurOrderStatus livreurStatus,
        RestaurantInfoDTO    restaurantInfoDTO,
        String livreurId
) {

}
