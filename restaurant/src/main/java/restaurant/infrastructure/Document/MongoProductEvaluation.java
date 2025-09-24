package restaurant.infrastructure.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "product_evaluations")
@CompoundIndex(name = "uniq_prod_client",
        def = "{'productId':1, 'clientId':1}",
        unique = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MongoProductEvaluation {
    @Id
    private String id;
    private String productId;
    private String clientId;
    private int rating;
    private String comment;
    private Instant createdAt;

}
