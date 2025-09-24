package restaurant.application.dto.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import restaurant.domain.model.ProductStatus;
import shared.enums.AvailabilityStatus;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {
    private String restaurantId;
    @NotBlank(message = "Le nom du produit est requis")
    private String name;
    private Double price;
    @NotBlank(message = "La description est obligatoire.")
    private String description;
    private String categoryId;
    private List<String> supplementIds;
    private List<String> ingredientIds;
    private String imageUrl;
    private ProductStatus status;
    private AvailabilityStatus availability;
    private String createdby;
    @NotNull(message = "Le prix du produit est requis")
    private double pricePreCom;
   // private double pricePostCom;
    private  Integer preptime;
    private String promotionId;

}
