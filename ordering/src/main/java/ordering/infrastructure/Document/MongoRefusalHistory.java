package ordering.infrastructure.Document;

import lombok.*;
import ordering.domain.model.RefusalReason;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("refusal_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MongoRefusalHistory {
    @Id
    private String id;
    private String orderId;
    private String livreurId;
    private RefusalReason reason;
    private Instant refusedAt;
}
