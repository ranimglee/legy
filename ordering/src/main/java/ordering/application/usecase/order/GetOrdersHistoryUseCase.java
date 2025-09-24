package ordering.application.usecase.order;

import lombok.RequiredArgsConstructor;
import ordering.domain.model.Order;
import ordering.domain.model.OrderStatus;
import ordering.domain.repository.OrderRepository;
import ordering.domain.model.value.RestaurantInfo;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import shared.domain.service.RestaurantQueryService;
import shared.dto.RestaurantInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GetOrdersHistoryUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetOrdersHistoryUseCase.class);

    private final OrderRepository orderRepository;
    private final RestaurantQueryService restaurantQueryService;

    public Page<Order> handle(String clientId, OrderStatus orderStatus, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Order> orders;

        if (orderStatus == null) {
            orders = orderRepository.findByClientIdOrderByCreatedAtDesc(clientId, pageable);
        } else {
            orders = orderRepository.findByClientIdAndOrderstatusOrderByCreatedAtDesc(clientId, orderStatus, pageable);
        }

        orders.forEach(order -> {
            try {
                RestaurantInfo restaurant = order.getRestaurant();
                if (restaurant == null || restaurant.getRestaurantId() == null) {
                    log.warn("Order ID={} has no valid restaurant reference", order.getId());
                    order.setRestaurant(null);
                    return;
                }

                RestaurantInfoDTO info = restaurantQueryService.getRestaurantInfoById(restaurant.getRestaurantId());

                // Populate all available fields
                restaurant.setName(info.name());
                restaurant.setPhone(info.phone());
                restaurant.setAddress(info.address());
                restaurant.setCommission(info.commission());
                restaurant.setLatitude(info.latitude());
                restaurant.setLongitude(info.longitude());
                restaurant.setLogo(info.logo());

            } catch (Exception e) {
                log.warn("Failed to fetch restaurant info for Order ID={} -> setting restaurant to null. Reason: {}",
                        order.getId(), e.getMessage());
                order.setRestaurant(null);
            }
        });

        return new PageImpl<>(orders, pageable, orders.size());
    }
}
