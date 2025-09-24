package restaurant.application.usecase.Product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Ingredient.IngredientResponseDTO;
import restaurant.application.dto.Product.ProductResponseDTO;
import restaurant.application.dto.Supplement.SupplementResponseDTO;
import restaurant.domain.service.ProductDomainService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllProductsUseCase {

    private final ProductDomainService productDomainService;

    public List<ProductResponseDTO> execute() {
        return productDomainService.getAllProducts()
                .stream()
                .map(product -> ProductResponseDTO.builder()
                        .id(product.getId())
                        .restaurantId(product.getRestaurantId())
                        .name(product.getName())
                        .pricePostCom(product.getPricePostCom())
                        .description(product.getDescription())
                        .categoryId(product.getCategoryId())
                        .supplements(product.getSupplements() != null
                                ? product.getSupplements().stream()
                                .map(s -> new SupplementResponseDTO(
                                        s.getId(), s.getName(), s.getDescription(), s.getPrice()))
                                .collect(Collectors.toList())
                                : null)
                        .ingredients(product.getIngredients() != null
                                ? product.getIngredients().stream()
                                .map(i -> new IngredientResponseDTO(
                                        i.getId(), i.getName(), i.getCategoryId())) // ✅ corrigé ici
                                .collect(Collectors.toList())
                                : null)
                        .imageUrl(product.getImageUrl())
                        .status(product.getStatus())
                        .availability(product.getAvailability())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
