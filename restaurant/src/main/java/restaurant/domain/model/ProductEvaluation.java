package restaurant.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder(toBuilder = true)
public class ProductEvaluation {
    String id;
    String productId;
    String clientId;
    String productName;
    int rating;
    String comment;
    Instant createdAt;
}
