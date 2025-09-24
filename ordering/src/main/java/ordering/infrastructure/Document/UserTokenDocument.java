package ordering.infrastructure.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.Instant;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTokenDocument {
    @Id
    private String id;
    private String userId;
    private String fcmToken;
    private Instant createdAt;
}
