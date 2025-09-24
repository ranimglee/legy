package restaurant.infrastructure.Document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@Document(collection = "supplements")
public class MongoSupplement extends BaseAuditDocument {

    @Id
    private String id;
    @Indexed(unique = false)
    private String name;
    private double price;
    private String description;
    private String createdby;
    private String restaurantId;



}
