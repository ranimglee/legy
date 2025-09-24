package restaurant.application.mapper;


import restaurant.application.dto.Ingredient.IngredientResponseDTO;
import restaurant.application.dto.Product.ProductResponseDTO;
import restaurant.application.dto.Supplement.SupplementResponseDTO;
import restaurant.domain.model.Product;

import java.util.List;
import java.util.stream.Collectors;

public class ProductResponseMapper {

    public static ProductResponseDTO toDTO(Product product) {
        List<SupplementResponseDTO> supplements = product.getSupplements() != null ?
                product.getSupplements().stream()
                        .map(s -> new SupplementResponseDTO(s.getId(), s.getName(),s.getDescription(), s.getPrice()))
                        .collect(Collectors.toList()) : null;

        List<IngredientResponseDTO> ingredients = product.getIngredients() != null ?
                product.getIngredients().stream()
                        .map(i -> new IngredientResponseDTO(
                                i.getId(), i.getName(),
                                i.getCategoryId(),
                                i.getCreatedby()

                        )).collect(Collectors.toList()) : null;

        return ProductResponseDTO.builder()
                .id(product.getId())
                .restaurantId(product.getRestaurantId())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .supplements(supplements)
                .ingredients(ingredients)
                .imageUrl(product.getImageUrl())
                .status(product.getStatus())
                .availability(product.getAvailability())
                .createdby(product.getCreatedby())
                .pricePostCom(product.getPricePostCom())
                .preptime(product.getPreptime())
                .promotionId(product.getPromotionId())
                .build();
    }
}
