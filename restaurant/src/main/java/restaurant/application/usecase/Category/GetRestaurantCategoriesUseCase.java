package restaurant.application.usecase.Category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Category.CategoryResponseDTO;
import restaurant.domain.model.Product;
import restaurant.domain.model.Category;
import restaurant.domain.service.ProductDomainService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetRestaurantCategoriesUseCase {

    private final ProductDomainService productDomainService;

    public List<CategoryResponseDTO> execute(String restaurantId) {
        return productDomainService.getProductsByRestaurantId(restaurantId).stream()
                .map(Product::getCategory)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        Category::getId,
                        c -> new CategoryResponseDTO(c.getId(), c.getName()),
                        (existing, replacement) -> existing  
                ))
                .values()
                .stream()
                .toList();
    }
}