package recommendation.infrastructure.persistence.search;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "search_history")
public class MongoSearchHistoryDocument {
    @Id
    private String id;
    private String clientId;
    private String keyword;
    private Instant searchedAt;
}
