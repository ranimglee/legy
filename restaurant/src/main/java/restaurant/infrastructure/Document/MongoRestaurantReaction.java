package restaurant.infrastructure.Document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import restaurant.domain.model.RestaurantReaction.ReactionType;

@Document(collection = "restaurant_reactions")
@CompoundIndex(name = "user_restaurant_idx", def = "{'userId': 1, 'restaurantId': 1}", unique = true)
@Getter @Setter
public class MongoRestaurantReaction {
    @Id
    private String id;
    private String restaurantId;
    private String userId;
    private ReactionType reaction;
}
