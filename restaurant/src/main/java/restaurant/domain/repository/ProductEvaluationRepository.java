package restaurant.domain.repository;

import restaurant.domain.model.ProductEvaluation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductEvaluationRepository {
    ProductEvaluation save(ProductEvaluation eval);

    Optional<ProductEvaluation> findById(String id);

    List<ProductEvaluation> findByProductId(String productId);

    Optional<ProductEvaluation> findByProductIdAndClientId(String productId, String clientId);
    Map<String, Double> getAverageRatingsForProducts(List<String> productIds);

    Map<String, Integer> getEvaluationCountsForProducts(List<String> productIds);
    void updateRatingAndCount(String productId, double averageRating, int reviewCount);

}
