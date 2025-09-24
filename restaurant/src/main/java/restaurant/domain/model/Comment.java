package restaurant.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class Comment {
    private String id;
    private String userId;
    private String targetId;
    private String targetType;
    private String parentId;
    private String content;
    private Instant createdAt;
}
