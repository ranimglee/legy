package restaurant.application.usecase.Product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Product.ProductRatingDTO;
import restaurant.application.service.ProductEvaluationService;

@Service
@RequiredArgsConstructor
public class GetProductRatingUseCase {

    private final ProductEvaluationService evaluationService;

    /**
     * Returns the current average rating and count for the given product.
     */
    public ProductRatingDTO execute(String productId) {
        var avgResp = evaluationService.averageForProduct(productId);
        long count = evaluationService.listForProduct(productId).size();
        return new ProductRatingDTO(
                productId,
                avgResp.averageRating(),
                count
        );
    }
}
