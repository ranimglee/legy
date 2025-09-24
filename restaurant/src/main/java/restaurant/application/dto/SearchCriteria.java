package restaurant.application.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import restaurant.domain.model.MainCuisineType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
    private String query;                         // free-text (min 3 chars)
    private MainCuisineType cuisineType;          // SENEGALAISE / INTERNATIONALE / …
    private String categoryId;                    // product category (burger, pizza…)
    private Double minRating;                     // ≥ this average restaurant rating
    private Double minPrice;                      // product price ≥
    private Double maxPrice;                      // product price ≤
}
