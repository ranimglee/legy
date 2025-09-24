package restaurant.domain.model;


import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseAuditDomain {

    private String id;
    private String name;
    private String createdby;
    private List<Product> products;
    private String restaurantId;


    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
