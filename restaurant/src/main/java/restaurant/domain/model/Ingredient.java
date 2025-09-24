package restaurant.domain.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Data
public class Ingredient extends BaseAuditDomain {

    private String id;

    private String name;

    private String categoryId;
    private String createdby;
    private String restaurantId;
    private String productId;



}
