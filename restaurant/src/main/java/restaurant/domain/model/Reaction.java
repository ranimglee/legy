package restaurant.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "reactions")
@Builder
@Getter @Setter
public class Reaction {
    private String id;

    private String userId;
    private String targetId;       // storyId or reelId
    private String targetType;     // 'story' or 'reel'
    private String reactionType;   // 'like' or 'dislike'
    private Instant reactedAt;
}
