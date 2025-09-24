package restaurant.application.dto.Menu;

import lombok.Getter;
import lombok.Setter;
import restaurant.application.dto.Product.ProductResponseDTO;

import java.util.List;

@Getter
@Setter
public class MenuGroupedItemResponseDTO {
    private String categoryId;
    private String categoryName;
    private List<ProductResponseDTO> products;

    public MenuGroupedItemResponseDTO(String categoryId, String categoryName, List<ProductResponseDTO> products) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.products = products;
    }
}
