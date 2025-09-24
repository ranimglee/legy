package restaurant.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
@Builder
public class ProductFilterCriteria {
    private Optional<String> categoryId;
    private Optional<Double> minPrice;
    private Optional<Double> maxPrice;
    private Optional<String> sortBy;
    private Optional<String> keyword;

    private int page;
    private int size;
}
