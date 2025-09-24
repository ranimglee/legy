package user.infrastructure.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import user.domain.model.Shift;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document(collection = "users")
public class MongoLivreurEntity extends MongoUserEntity {
    private String rib;
    private String matricule;
    private String address;

    @DBRef
    private List<ShiftDocument> shifts;
    private Boolean isAssigned;
    @Override
    public String getRole() {
        return "LIVREUR";
    }
}
