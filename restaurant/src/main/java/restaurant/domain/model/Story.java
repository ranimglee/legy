package restaurant.domain.model;


import lombok.*;

import java.time.Instant;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Story {
    private String id;
    private String userId;
    private String restaurantId;
    private String s3Key;
    private String url;
    private Instant uploadedAt;
    private Long viewCount;


}
