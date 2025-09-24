package restaurant.application.dto.Restaurant;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class AvisResponseDTO {
    private String id;
    private String userId;
    private int score;
    private String comment;
    private Instant createdAt;
}
