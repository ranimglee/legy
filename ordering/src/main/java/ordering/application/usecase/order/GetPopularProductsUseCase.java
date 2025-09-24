package ordering.application.usecase.order;

import lombok.RequiredArgsConstructor;
import ordering.application.dto.order.PopularProductDTO;
import ordering.domain.model.Order;
import ordering.domain.repository.OrderRepository;
import shared.dto.ProductInfo;
import shared.port.ProductQueryPort;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class GetPopularProductsUseCase {

    private final OrderRepository orderRepository;
    private final ProductQueryPort productQueryPort;   // ← nouveau

    public List<PopularProductDTO> handle(int limit) {

        List<Order> allOrders = orderRepository.findAll();

        /* 1) Compter les quantités par produit */
        Map<String, Integer> qtyByProduct = new HashMap<>();
        for (Order order : allOrders) {
            order.getItems().forEach(item ->
                    qtyByProduct.merge(item.getProductId(),
                            item.getQuantity(),
                            Integer::sum));
        }

        /* 2) Trier, limiter, enrichir puis mapper vers DTO */
        return qtyByProduct.entrySet().stream()
                .sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    // Enrichir via le port partagé
                    ProductInfo info = productQueryPort.getProductById(entry.getKey());
                    return new PopularProductDTO(
                            entry.getKey(),
                            info != null && info.getName() != null ? info.getName() : "Nom inconnu",
                            info != null ? info.getImageUrl() : "",
                            entry.getValue()
                    );

                })
                .toList();
    }
}
