package restaurant.application.dto.Product;

import lombok.*;
import restaurant.application.dto.Ingredient.IngredientResponseDTO;
import restaurant.application.dto.Supplement.SupplementResponseDTO;
import restaurant.domain.model.ProductStatus;
import shared.enums.AvailabilityStatus;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {

    private String id;
    private String restaurantId;

    private String name;
    private String description;
    private String categoryId;

    private List<SupplementResponseDTO> supplements;
    private List<IngredientResponseDTO> ingredients;

    private String imageUrl;
    private ProductStatus status;
    private AvailabilityStatus availability;

    private String createdby;
    private double pricePostCom;
    private Integer preptime;
    private String promotionId;


    public ProductResponseDTO(String id, String name, double price, String description, String categoryId, ProductStatus status) {
        this.id = id;
        this.name = name;
        this.pricePostCom = price;
        this.description = description;
        this.categoryId = categoryId;
        this.status = status;
    }

    public ProductResponseDTO(String id, String name, double price, String description, String categoryId, List<SupplementResponseDTO> supplements, ProductStatus status) {
        this.id = id;
        this.name = name;
        this.pricePostCom = price;
        this.description = description;
        this.categoryId = categoryId;
        this.supplements = supplements;
        this.status = status;

    }

    public ProductResponseDTO(String id, String name, double price, String description, String categoryId,
                              List<SupplementResponseDTO> supplements,
                              List<IngredientResponseDTO> ingredients, ProductStatus status) {
        this.id = id;
        this.name = name;
        this.pricePostCom = price;
        this.description = description;
        this.categoryId = categoryId;
        this.supplements = supplements;
        this.ingredients = ingredients;
        this.status = status;

    }

    public ProductResponseDTO(String id, String name, double price, String description, String categoryId,
                              List<SupplementResponseDTO> supplements,
                              List<IngredientResponseDTO> ingredients,
                              String imageUrl, ProductStatus status) {
        this.id = id;
        this.name = name;
        this.pricePostCom = price;
        this.description = description;
        this.categoryId = categoryId;
        this.supplements = supplements;
        this.ingredients = ingredients;
        this.imageUrl = imageUrl;
        this.status = status;

    }

    public ProductResponseDTO(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.pricePostCom = price;

    }

    public ProductResponseDTO(String id, String name, double price, String description, String categoryId,
                              List<SupplementResponseDTO> supplements, List<IngredientResponseDTO> ingredients,
                              String imageUrl, ProductStatus status, AvailabilityStatus availability) {
        this.id = id;
        this.name = name;
        this.pricePostCom = price;
        this.description = description;
        this.categoryId = categoryId;
        this.supplements = supplements;
        this.ingredients = ingredients;
        this.imageUrl = imageUrl;
        this.status = status;
        this.availability = availability;

    }


}
