package recommendation.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistoryEntity {
    @Id
    private String id;

    private String clientId;
    private String keyword;
    private Instant searchedAt;
}