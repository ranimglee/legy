package recommendation.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FullProductRecommendationDTO {

    @JsonProperty("restaurant_id")
    private String restaurantId;

    @JsonProperty("products")
    private List<ProductDTO> products;

    @Data
    public static class ProductDTO {

        @JsonProperty("_id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("description")
        private String description;

        @JsonProperty("pricePostCom")
        private Integer pricePostCom;

        @JsonProperty("category")
        private String category;

        @JsonProperty("restaurantId")
        private String restaurantId;

        @JsonProperty("averageRating")
        private Double averageRating;

        @JsonProperty("reviewCount")
        private Integer reviewCount;

        @JsonProperty("imageUrl")
        private String imageUrl;

        @JsonProperty("supplements")
        private List<String> supplements;
    }
}
