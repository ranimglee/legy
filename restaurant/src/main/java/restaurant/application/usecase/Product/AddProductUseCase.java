package restaurant.application.usecase.Product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Ingredient.IngredientResponseDTO;
import restaurant.application.dto.Product.ProductRequestDTO;
import restaurant.application.dto.Product.ProductResponseDTO;
import restaurant.application.dto.Supplement.SupplementResponseDTO;
import restaurant.domain.model.Category;
import restaurant.domain.model.Product;
import restaurant.domain.model.ProductStatus;
import restaurant.domain.model.Restaurant;
import restaurant.domain.repository.RestaurantRepository;
import restaurant.domain.service.ProductDomainService;
import shared.enums.AvailabilityStatus;


@Service
@RequiredArgsConstructor
public class AddProductUseCase {

    private final ProductDomainService productDomainService;
    private final RestaurantRepository restaurantRepository;

    public ProductResponseDTO execute(ProductRequestDTO request) {
        Restaurant restaurant = restaurantRepository
                .findByCreatedBy(request.getCreatedby())
                .orElseThrow(() -> new RuntimeException(
                        "Aucun restaurant associé à l’utilisateur " + request.getCreatedby()));

        Product product = new Product();
        product.setName(request.getName());
        product.setPricePreCom(request.getPricePreCom());
        product.setDescription(request.getDescription());
        product.setCategoryId(request.getCategoryId());
        product.setImageUrl(request.getImageUrl());
        product.setStatus(request.getStatus() != null ? request.getStatus() : ProductStatus.ACCEPTED);
        product.setAvailability(request.getAvailability() != null ? request.getAvailability() : AvailabilityStatus.AVAILABLE);
        product.setCreatedby(request.getCreatedby());
        product.setRestaurantId(restaurant.getId());
        product.setPreptime(request.getPreptime());
        product.setPricePostCom(product.getPricePreCom() +
                ((product.getPricePreCom() * restaurant.getCommission()) / 100));

        Product savedProduct = productDomainService.addProduct(product, request.getSupplementIds(), request.getIngredientIds());

        // Récupère les supplements complets à partir des supplementIds
        var supplementDTOs = request.getSupplementIds() != null && !request.getSupplementIds().isEmpty()
                ? productDomainService.getSupplementsByProductId(savedProduct.getId())
                : null;

        // Récupère les ingredients complets à partir des ingredientIds
        var ingredientDTOs = request.getIngredientIds() != null && !request.getIngredientIds().isEmpty()
                ? productDomainService.getIngredientByProductId(savedProduct.getId())
                : null;

        return ProductResponseDTO.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .pricePostCom(savedProduct.getPricePostCom())
                .description(savedProduct.getDescription())
                .categoryId(savedProduct.getCategoryId())
                .supplements(supplementDTOs)
                .ingredients(ingredientDTOs)
                .imageUrl(savedProduct.getImageUrl())
                .status(savedProduct.getStatus())
                .restaurantId(savedProduct.getRestaurantId())
                .availability(savedProduct.getAvailability())
                .createdby(savedProduct.getCreatedby())
                .preptime(savedProduct.getPreptime())
                .build();
    }
}
