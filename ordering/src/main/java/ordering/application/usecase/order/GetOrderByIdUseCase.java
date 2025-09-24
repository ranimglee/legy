package ordering.application.usecase.order;

import ordering.application.dto.order.OrderDetailsDTO;

public interface GetOrderByIdUseCase {
    OrderDetailsDTO handle(String orderId, String clientId);
}
