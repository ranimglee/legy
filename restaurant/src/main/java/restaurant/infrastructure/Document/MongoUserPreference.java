package restaurant.infrastructure.Document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "user_preferences")
@Getter
@Setter
public class MongoUserPreference {
    @Id
    private String id;
    @Indexed(unique = true)
    private String userId;
    private List<String> categoryNames;
    private List<String> cuisineTypes;
}