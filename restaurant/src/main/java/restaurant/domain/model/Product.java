package restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shared.enums.AvailabilityStatus;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseAuditDomain {

    private String id;
    private String restaurantId;

    private String name;
    private double pricePreCom;
    private double pricePostCom;
    private String description;

    private String categoryId;
    private List<String> supplementIds;
    private List<String> ingredientIds;

    private List<Supplement> supplements;
    private List<Ingredient> ingredients;


    private Category category;
    private String imageUrl;
    private ProductStatus status;
    private AvailabilityStatus availability;
    private String createdby;
    private  Integer preptime;
    private String promotionId;

    private double averageRating;
    private int reviewCount;


}
