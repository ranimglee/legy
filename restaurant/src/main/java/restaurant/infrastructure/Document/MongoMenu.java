package restaurant.infrastructure.Document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document(collection = "menus")
public class MongoMenu extends BaseAuditDocument {

    @Id
    private String id;
    @TextIndexed
    private String name;
    @TextIndexed
    private String description;

    @DBRef
    private List<MongoCategory> categories;

    private String restaurantId;
    private String createdby;


}
