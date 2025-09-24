package restaurant.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.EvaluateProduct.AverageProductRatingResponse;
import restaurant.application.dto.EvaluateProduct.EvaluateProductRequest;
import restaurant.application.dto.EvaluateProduct.EvaluateProductResponse;
import restaurant.application.exception.ProductNotFoundException;
import restaurant.domain.model.ProductEvaluation;
import restaurant.domain.repository.ProductEvaluationRepository;
import restaurant.domain.repository.ProductRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductEvaluationService {

    private final ProductEvaluationRepository repo;
    private final ProductRepository productRepo;

    public EvaluateProductResponse evaluate(String productId, String clientId, EvaluateProductRequest req) {
        Instant now = Instant.now();

        productRepo.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found"));
        Optional<ProductEvaluation> existing = repo.findByProductIdAndClientId(productId, clientId);

        ProductEvaluation toSave;
        if (existing.isPresent()) {

            toSave = existing.get().toBuilder()
                    .rating(req.rating())
                    .comment(req.comment())
                    .createdAt(now)
                    .build();
        } else {
            toSave = ProductEvaluation.builder()
                    .id(UUID.randomUUID().toString())
                    .productId(productId)
                    .clientId(clientId)
                    .productName(productRepo.findById(productId).get().getName())
                    .rating(req.rating())
                    .comment(req.comment())
                    .createdAt(now)
                    .build();
        }

        ProductEvaluation saved = repo.save(toSave);
        updateProductRating(productId);
        return new EvaluateProductResponse(
                saved.getProductId(),
                saved.getClientId(),
                saved.getRating(),
                saved.getComment(),
                saved.getCreatedAt().toString()
        );
    }
    public List<EvaluateProductResponse> listForProduct(String productId) {
        return repo.findByProductId(productId)
                .stream()
                .map(e -> new EvaluateProductResponse(
                        e.getProductId(),
                        e.getClientId(),
                        e.getRating(),
                        e.getComment(),
                        e.getCreatedAt().toString()
                ))
                .collect(Collectors.toList());
    }

    public AverageProductRatingResponse averageForProduct(String productId) {
        var all = repo.findByProductId(productId);
        double avg = all.stream()
                .mapToInt(ProductEvaluation::getRating)
                .average()
                .orElse(0.0);

        return new AverageProductRatingResponse(productId, avg);
    }
    public void updateProductRating(String productId) {
        List<ProductEvaluation> evaluations = repo.findByProductId(productId);

        double average = evaluations.stream()
                .mapToInt(ProductEvaluation::getRating)
                .average()
                .orElse(0.0);

        int count = evaluations.size();

        repo.updateRatingAndCount(productId, average, count);
    }

}
