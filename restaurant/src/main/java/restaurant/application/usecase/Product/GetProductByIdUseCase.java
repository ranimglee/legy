package restaurant.application.usecase.Product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Ingredient.IngredientResponseDTO;
import restaurant.application.dto.Product.ProductResponseDTO;
import restaurant.application.dto.Supplement.SupplementResponseDTO;
import restaurant.application.exception.ProductNotFoundException;
import restaurant.domain.model.Product;
import restaurant.domain.service.IngredientDomainService;
import restaurant.domain.service.ProductDomainService;
import restaurant.domain.service.SupplementDomainService;


import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetProductByIdUseCase {

    private final ProductDomainService productDomainService;
    private final SupplementDomainService supplementDomainService;
    private final IngredientDomainService ingredientDomainService;

    public ProductResponseDTO execute(String id) {
        Product product = productDomainService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        // Charger supplements depuis les IDs
        var supplements = product.getSupplementIds() == null
                ? null
                : supplementDomainService.getSupplementsByIds(product.getSupplementIds());

        // Charger ingredients depuis les IDs
        var ingredients = product.getIngredientIds() == null
                ? null
                : ingredientDomainService.getIngredientsByIds(product.getIngredientIds());

        return ProductResponseDTO.builder()
                .id(product.getId())
                .restaurantId(product.getRestaurantId())
                .name(product.getName())
                .pricePostCom(product.getPricePostCom())
                .description(product.getDescription())
                .categoryId(product.getCategoryId())
                .supplements(supplements == null ? null : supplements.stream()
                        .map(s -> new SupplementResponseDTO(
                                s.getId(), s.getName(), s.getDescription(), s.getPrice()))
                        .collect(Collectors.toList()))
                .ingredients(ingredients == null ? null : ingredients.stream()
                        .map(i -> new IngredientResponseDTO(
                                i.getId(), i.getName(), i.getCategoryId(), i.getCreatedby()))
                        .collect(Collectors.toList()))
                .imageUrl(product.getImageUrl())
                .status(product.getStatus())
                .availability(product.getAvailability())
                .build();
    }
}
