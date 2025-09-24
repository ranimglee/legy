package restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RestaurantReaction {
    private String restaurantId;
    private String userId;
    private ReactionType reaction;

    public enum ReactionType {
        LIKE, DISLIKE
    }
}
