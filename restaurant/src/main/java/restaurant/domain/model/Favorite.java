package restaurant.domain.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a user’s favorite restaurant.
 */
@Getter
@Setter
public class Favorite {
    private String id;
    private String userId;
    private String restaurantId;
}
