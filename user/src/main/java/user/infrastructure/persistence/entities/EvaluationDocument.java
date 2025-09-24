package user.infrastructure.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "livreur-evaluations")
@Data
@CompoundIndex(name = "unique_eval", def = "{'livreurId':1, 'clientId':1}", unique = true)
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationDocument {
    @Id
    private String id;
    private String livreurId;
    private String clientId;
    private int rating;
    private String comment;
    private Instant createdAt;
}
