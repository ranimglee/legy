package restaurant.application.usecase.Product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Product.ProductSummaryDTO;
import restaurant.domain.service.ProductDomainService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetProductsByRestaurantAndCategoryUseCase {

    private final ProductDomainService productDomainService;

    public List<ProductSummaryDTO> execute(
            String restaurantId,
            String categoryId
    ) {
        return productDomainService
                .getProductsByRestaurantAndCategory(restaurantId, categoryId)
                .stream()
                .map(p -> new ProductSummaryDTO(
                        p.getId(),
                        p.getName(),
                        p.getImageUrl(),
                        p.getPricePostCom(),
                        p.getDescription()


                ))
                .collect(Collectors.toList());
    }
}
