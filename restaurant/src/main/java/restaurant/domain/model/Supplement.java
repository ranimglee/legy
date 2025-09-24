package restaurant.domain.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Supplement extends BaseAuditDomain {

    private String id;

    private String name;
    private double price;
    private String description;
    private String createdby;
    private String restaurantId;

}
