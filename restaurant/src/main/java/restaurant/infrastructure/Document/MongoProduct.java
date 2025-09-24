package restaurant.infrastructure.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import restaurant.domain.model.ProductStatus;
import shared.enums.AvailabilityStatus;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndexes({
        @CompoundIndex(name = "avgRating_desc", def = "{'averageRating': -1}"),
        @CompoundIndex(name = "reviewCount_desc", def = "{'reviewCount': -1}")
})
@Document(collection = "products")
public class MongoProduct extends BaseAuditDocument {

    @Id
    private String id;
    private String restaurantId;

    @TextIndexed
    private String name;
    private double pricePreCom;
    private double pricePostCom;
    @TextIndexed
    private String description;

    private String categoryId;
    private MongoCategory category;


    private List<String> supplementIds;
    private List<String> ingredientIds;

    private List<MongoSupplement> supplements;
    private List<MongoIngredient> ingredients;

    private String imageUrl;
    private ProductStatus status;

    private AvailabilityStatus availability;
    private String createdby;
    private Integer preptime;
    private String promotionId;

    private double averageRating;
    private int reviewCount;
}
