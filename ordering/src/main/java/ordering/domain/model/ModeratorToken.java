package ordering.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ModeratorToken {
    private String id;

    private String moderatorId;

    private String fcmToken;

    private Instant createdAt;


}
