package restaurant.infrastructure.Document;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "stories")
@Builder
@Getter
@Setter
public class MongoStory {
    @Id
    private String id;

    private String userId;
    private String restaurantId;
    private String s3Key;
    private String url;
    private Instant uploadedAt;
    private Long viewCount;
}