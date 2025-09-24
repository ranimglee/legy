package restaurant.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductFavorite {
    private String id;
    private String userId;
    private String productId;
}
