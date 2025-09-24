package restaurant.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class Avis {
    private String id;
    private String restaurantId;
    private String userId;
    private int score;       // 1–5
    private String comment;
    private Instant createdAt = Instant.now();
}
