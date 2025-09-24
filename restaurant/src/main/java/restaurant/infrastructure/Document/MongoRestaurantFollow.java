package restaurant.infrastructure.Document;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document(collection = "restaurant_follows")
public class MongoRestaurantFollow {
    @Id
    private String id;
    private String clientId;
    private String restaurantId;
    private Instant followedAt;
}
