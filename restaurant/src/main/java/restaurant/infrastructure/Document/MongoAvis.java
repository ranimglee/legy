package restaurant.infrastructure.Document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "avis-restaurant")
@CompoundIndex(name = "ux_restaurant_user", def = "{'restaurantId':1, 'userId':1}", unique = true)
@Getter
@Setter
public class MongoAvis {
    @Id
    private String id;
    private String restaurantId;
    private String userId;
    private int score;
    private String comment;
    private Instant createdAt;
    private Instant updatedAt;
}
