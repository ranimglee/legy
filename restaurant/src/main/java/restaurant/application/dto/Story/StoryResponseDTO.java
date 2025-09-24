package restaurant.application.dto.Story;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class StoryResponseDTO {
    private String id;
    private String userId;
    private String s3Key;
    private String url;
    private Instant uploadedAt;
    private Long viewCount;
    private String restaurantId;
    private String restaurantName;
    private String restaurantLogo;
}
