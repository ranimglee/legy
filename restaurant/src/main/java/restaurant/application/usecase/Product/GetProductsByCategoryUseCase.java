package restaurant.application.usecase.Product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Ingredient.IngredientResponseDTO;
import restaurant.application.dto.Product.ProductResponseDTO;
import restaurant.application.dto.Supplement.SupplementResponseDTO;
import restaurant.domain.model.Product;
import restaurant.domain.service.IngredientDomainService;
import restaurant.domain.service.ProductDomainService;
import restaurant.domain.service.SupplementDomainService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetProductsByCategoryUseCase {

    private final ProductDomainService productDomainService;
    private final SupplementDomainService supplementDomainService;
    private final IngredientDomainService ingredientDomainService;

    public List<ProductResponseDTO> execute(String categoryId) {
        List<Product> products = productDomainService.getProductsByCategoryId(categoryId);

        return products.stream()
                .map(product -> {
                    // Charger supplements via les IDs
                    List<SupplementResponseDTO> supplements = product.getSupplementIds() == null
                            ? null
                            : supplementDomainService.getSupplementsByIds(product.getSupplementIds()).stream()
                            .map(s -> new SupplementResponseDTO(
                                    s.getId(),
                                    s.getName(),
                                    s.getDescription(),
                                    s.getPrice()
                            ))
                            .collect(Collectors.toList());

                    // Charger ingredients via les IDs
                    List<IngredientResponseDTO> ingredients = product.getIngredientIds() == null
                            ? null
                            : ingredientDomainService.getIngredientsByIds(product.getIngredientIds()).stream()
                            .map(i -> new IngredientResponseDTO(
                                    i.getId(),
                                    i.getName(),
                                    i.getCategoryId(),
                                    i.getCreatedby()
                            ))
                            .collect(Collectors.toList());

                    return ProductResponseDTO.builder()
                            .id(product.getId())
                            .restaurantId(product.getRestaurantId())
                            .name(product.getName())
                            .pricePostCom(product.getPricePostCom())
                            .description(product.getDescription())
                            .categoryId(product.getCategoryId())
                            .supplements(supplements)
                            .ingredients(ingredients)
                            .imageUrl(product.getImageUrl())
                            .status(product.getStatus())
                            .availability(product.getAvailability())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
