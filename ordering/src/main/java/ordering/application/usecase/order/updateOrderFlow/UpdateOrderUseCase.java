package ordering.application.usecase.order.updateOrderFlow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ordering.application.dto.order.UpdateOrderRequestDTO;
import ordering.application.exception.OrderNotFoundException;
import ordering.domain.model.LivreurOrderStatus;
import ordering.domain.model.Order;
import ordering.domain.model.OrderStatus;
import ordering.domain.model.PaymentStatus;
import ordering.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderItemUpdater orderItemUpdater;
    private final DistanceCalculator distanceCalculator;
    private final OrderStatusUpdater orderStatusUpdater;
    private final LivreurAssignmentService livreurAssignmentService;
    private final DeliveryHandler deliveryHandler;
    private final OrderNotificationService notificationService;

    public Order handle(String orderId, UpdateOrderRequestDTO dto) {
        // 1. Fetch order from repository
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        log.info("Order items before update: {}", order.getItems());

        // Update items if provided
        orderItemUpdater.updateItems(order, dto);

        // Calculate distance and ETA
        var distanceResult = distanceCalculator.calculateDistance(order);
        double km = distanceResult.km();
        String eta = distanceResult.eta();
        int etaMinutes = distanceResult.etaMinutes();

        // Update order status
        orderStatusUpdater.updateStatus(order, dto);

        // Handle status-specific logic
        OrderStatus nextStatus = order.getOrderStatus();
        if (nextStatus == OrderStatus.ACCEPTED) {
            notificationService.notifyClientOrderAccepted(order);
        } else if (nextStatus == OrderStatus.REFUSED) {
            notificationService.notifyClientOrderRefused(order);
        } else if (nextStatus == OrderStatus.DELIVERED) {
            deliveryHandler.handleDelivery(order, km, etaMinutes);
        } else if (nextStatus == OrderStatus.PREPARING) {
            livreurAssignmentService.assignLivreur(order, etaMinutes);
        }

        // Handle picked up status
        if (dto.livreurStatus() == LivreurOrderStatus.PICKED_UP) {
            notificationService.notifyLivreurStartTracking(order);
            notificationService.notifyClientOrderPickedUp(order);
        }

        // Update payment status if provided
        if (dto.isPrepaid() != null) {
            order.setPaymentStatus(dto.isPrepaid() ? PaymentStatus.PAID : PaymentStatus.PENDING);
        }

        // Update livreur status if provided
        if (dto.livreurStatus() != null) {
            order.setLivreurStatus(dto.livreurStatus());
        }

        log.info("Order items before save: {}", order.getItems());
        Order savedOrder = orderRepository.save(order);
        log.info("Order items after save: {}", savedOrder.getItems());
        return savedOrder;
    }

    public Order forceUpdateStatus(String orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        order.setOrderStatus(newStatus);
        log.info("Order items before force update save: {}", order.getItems());
        Order savedOrder = orderRepository.save(order);
        log.info("Order items after force update save: {}", savedOrder.getItems());
        return savedOrder;
    }
}