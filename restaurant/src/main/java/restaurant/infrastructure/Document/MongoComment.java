package restaurant.infrastructure.Document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "comments")
@Builder
@Getter
@Setter
public class MongoComment {
    @Id
    private String id;

    private String userId;
    private String targetId;
    private String targetType;
    private String parentId;
    private String content;
    private Instant createdAt;

}
