package user.infrastructure.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document(collection = "users")
public class MongoClientEntity extends MongoUserEntity {
    private String address;
    private user.domain.model.AuthProvider provider = user.domain.model.AuthProvider.LOCAL;

    private String guestSessionId;

    @Override
    public String getRole() {
        return "CLIENT";
    }
}
