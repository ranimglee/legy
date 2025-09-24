package user.infrastructure.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "driver_zone_assignments")
public class MongoDriverZoneAssignment {

    @Id
    private String id;

    private String livreurId; 

    private String zoneId;
    private boolean assigned;
    private Double latitude;
    private Double longitude;
}