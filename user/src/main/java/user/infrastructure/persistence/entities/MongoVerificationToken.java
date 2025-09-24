package user.infrastructure.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "verification_tokens")
public class MongoVerificationToken {

    @Id
    private String id = UUID.randomUUID().toString();

    private String token;
    private String userId;
    private LocalDateTime expiresAt;
}
