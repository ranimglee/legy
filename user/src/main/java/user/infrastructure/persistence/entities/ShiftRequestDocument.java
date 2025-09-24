package user.infrastructure.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import user.domain.model.Shift;
import user.domain.model.ShiftStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "shiftRequests")
public class ShiftRequestDocument {
    private String id;
    private String livreurId;
    private String zoneId;
    private ShiftDocument shift; // reference to Shift
    private ShiftStatus status;

}
