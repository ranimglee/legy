package restaurant.infrastructure.Document;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "favorites-restaurants")
@Getter
@Setter
public class MongoFavorite {
    @Id
    private String id;
    private String userId;
    private String restaurantId;
}
