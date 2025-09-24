package restaurant.infrastructure.Document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import restaurant.domain.model.Product;

import java.util.List;

@Document(collection = "Category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MongoCategory extends BaseAuditDocument {
    @Id
    private String id;
    @TextIndexed
    @Indexed(unique = false)
    private String name;
    private String createdby;

    @DBRef(lazy = true)
    private List<Product> products;
    private String restaurantId;



}
