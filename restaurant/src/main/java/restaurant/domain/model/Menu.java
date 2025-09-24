package restaurant.domain.model;

import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
public class Menu extends BaseAuditDomain {

    private String id;
    private String name;
    private String description;

    private List<Category> categories;

    private String restaurantId;
    private String createdby;


}
