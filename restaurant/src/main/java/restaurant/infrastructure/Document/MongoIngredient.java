package restaurant.infrastructure.Document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@Document(collection = "ingredients")
public class MongoIngredient extends BaseAuditDocument {

    @Id
    private String id;
    @Indexed(unique = false)
    private String name;
    private String createdby;
    @DBRef
    private MongoCategory category;
    private String restaurantId;
    private String productId;
    private String categoryId;


}


