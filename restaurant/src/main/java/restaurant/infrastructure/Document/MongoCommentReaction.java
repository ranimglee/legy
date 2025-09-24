package restaurant.infrastructure.Document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "comment_reactions")
@Getter
@Setter
@Builder
public class MongoCommentReaction {
    @Id
    private String id;
    private String commentId;
    private String userId;
    private Instant createdAt;
}
