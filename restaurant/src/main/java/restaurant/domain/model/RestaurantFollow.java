package restaurant.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class RestaurantFollow {
    private String id;
    private String clientId;
    private String restaurantId;
    private Instant followedAt;
}
