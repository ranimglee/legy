package ordering.application.usecase.order;

import ordering.application.dto.order.OrderTrackingDTO;

public interface GetOrderTrackingUseCase {
    OrderTrackingDTO handle(String orderId);
}