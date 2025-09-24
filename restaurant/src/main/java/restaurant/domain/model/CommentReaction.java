package restaurant.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class CommentReaction {
    private String id;
    private String commentId;
    private String userId;
    private Instant createdAt;
}
