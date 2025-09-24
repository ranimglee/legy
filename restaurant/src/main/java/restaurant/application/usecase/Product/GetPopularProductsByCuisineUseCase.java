package restaurant.application.usecase.Product;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Product.ProductAllSummaryDTO;

import restaurant.application.dto.Product.ProductListDTO;
import restaurant.application.service.ProductEvaluationService;
import restaurant.domain.model.InternationalCuisine;
import restaurant.domain.model.MainCuisineType;
import restaurant.domain.model.Product;
import restaurant.domain.model.Restaurant;
import restaurant.domain.repository.ProductRepository;
import restaurant.domain.repository.RestaurantRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GetPopularProductsByCuisineUseCase {

    private final RestaurantRepository restaurantRepo;
    private final ProductRepository productRepo;
    private final ProductEvaluationService evaluationService;

    @Cacheable(
            value = "popular",
            key = "'main=' + #main + '::sub=' + #sub + '::page=' + #page + '::size=' + #size"
    )

    public ProductListDTO execute(MainCuisineType main, InternationalCuisine sub, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<String> restIds = restaurantRepo.findByCuisine(main, sub, Pageable.unpaged())
                .getContent()
                .stream()
                .map(Restaurant::getId)
                .toList();

        // Fetch only paginated products
        List<Product> products = productRepo.findByRestaurantIds(restIds, pageable)
                .getContent();

        List<String> productIds = products.stream().map(Product::getId).toList();

        List<ProductAllSummaryDTO> enriched = products.stream()
                .map(p -> new ProductAllSummaryDTO(
                        p.getId(),
                        p.getName(),
                        p.getImageUrl(),
                        p.getPricePostCom(),
                        p.getDescription(),
                        p.getAverageRating(),
                        p.getReviewCount()
                ))
                .sorted((a, b) -> Double.compare(b.averageRating(), a.averageRating()))
                .toList();


        return new ProductListDTO(enriched);
    }
}

