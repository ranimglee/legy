package ordering.application.usecase.order;

import lombok.RequiredArgsConstructor;
import ordering.application.dto.order.PopularProductDTO;
import ordering.domain.model.Order;
import ordering.domain.repository.OrderRepository;
import shared.port.ProductQueryPort;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class GetPopularProductsByRestoUseCase {

    private final OrderRepository orderRepository;
    private final ProductQueryPort productQueryPort;   // ← NEW

    public List<PopularProductDTO> handle(String restaurantId, int limit) {

        /* 1 – Load only this restaurant’s orders */
        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);

        /* 2 – Count quantities per product */
        Map<String, Integer> qtyByProduct = new HashMap<>();
        orders.forEach(o -> o.getItems()
                .forEach(i ->
                        qtyByProduct.merge(i.getProductId(),
                                i.getQuantity(),
                                Integer::sum)));

        /* 3 – Rank, enrich via shared.port, map to DTO */
        return qtyByProduct.entrySet().stream()
                .sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    var info = productQueryPort.getProductById(entry.getKey());
                    return new PopularProductDTO(
                            info.getProductId(),
                            info.getName(),
                            info.getImageUrl(),
                            entry.getValue());
                })
                .toList();
    }
}
