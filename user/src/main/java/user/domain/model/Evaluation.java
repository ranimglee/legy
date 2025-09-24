package user.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder(toBuilder = true)
public class Evaluation {
    String id;
    String livreurId;
    String clientId;
    int rating;
    String comment;
    Instant createdAt;
}
