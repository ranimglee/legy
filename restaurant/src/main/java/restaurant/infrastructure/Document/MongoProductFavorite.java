package restaurant.infrastructure.Document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "product_favorites")
@Getter
@Setter
public class MongoProductFavorite {
    @Id
    private String id;
    private String userId;
    private String productId;
}
