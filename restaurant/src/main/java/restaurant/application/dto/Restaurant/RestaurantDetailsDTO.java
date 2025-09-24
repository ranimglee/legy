package restaurant.application.dto.Restaurant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import restaurant.application.dto.Product.ProductDTO;
import restaurant.application.dto.Product.ProductResponseDTO;
import restaurant.domain.model.RestaurantStatus;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RestaurantDetailsDTO {
    private String id;
    private String name;
    private String email;
    private String address;
    private RestaurantStatus status;
    private long totalOrders;
    private double totalRevenue;
    private double averageRating;
    private List<ProductDTO> products;
   // private List<PromotionDTO> promotions;
}