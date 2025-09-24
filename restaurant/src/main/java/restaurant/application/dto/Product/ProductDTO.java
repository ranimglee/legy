package restaurant.application.dto.Product;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import restaurant.domain.model.ProductStatus;

@Getter
@Setter
@AllArgsConstructor
public class ProductDTO {
    private String id;
    private String name;
    private double pricePostCom;
    private double pricePreCom;
    private ProductStatus status;
    private String imageUrl;

}