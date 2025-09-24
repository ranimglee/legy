package ordering.application.usecase.order;

import lombok.RequiredArgsConstructor;
import ordering.application.dto.order.CategorySalesDTO;
import ordering.domain.model.Order;
import ordering.domain.repository.OrderRepository;
import org.springframework.stereotype.Component;
import shared.dto.ProductCategoryInfo;
import shared.port.ProductQueryPort;

import java.util.*;

@Component
@RequiredArgsConstructor
public class GetMostSoldCategoriesUseCase {

    private final OrderRepository orderRepository;
    private final ProductQueryPort productQueryPort;

    /**
     * @param restaurantId the restaurant whose categories we want to rank
     * @param limit        max number of categories to return
     */
    public List<CategorySalesDTO> handle(String restaurantId, int limit) {

        /* 1 – Load only this restaurant’s orders */
        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);

        /* 2 – Extract every distinct productId from those orders */
        Set<String> productIds = new HashSet<>();
        orders.forEach(o -> o.getItems()
                .forEach(i -> productIds.add(i.getProductId())));

        /* 3 – Resolve product → category in one batch */
        Map<String, ProductCategoryInfo> productCategory =
                productQueryPort.getCategoryInfoForProducts(productIds);

        /* 4 – Count quantity per category */
        Map<String, Integer> qtyByCategory = new HashMap<>();
        Map<String, String> categoryNames = new HashMap<>();

        orders.forEach(o -> o.getItems().forEach(item -> {
            var info = productCategory.get(item.getProductId());
            if (info != null && info.categoryId() != null) {
                qtyByCategory.merge(info.categoryId(), item.getQuantity(), Integer::sum);
                categoryNames.putIfAbsent(info.categoryId(), info.categoryName());
            }
        }));

        /* 5 – Sort, limit, map to DTO */
        return qtyByCategory.entrySet().stream()
                .sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
                .limit(limit)
                .map(e -> new CategorySalesDTO(
                        e.getKey(),
                        categoryNames.getOrDefault(e.getKey(), "Unknown"),
                        e.getValue()))
                .toList();
    }
}
