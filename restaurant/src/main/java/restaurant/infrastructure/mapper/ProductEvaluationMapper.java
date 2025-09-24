package restaurant.infrastructure.mapper;

import org.springframework.stereotype.Component;
import restaurant.application.dto.EvaluateProduct.EvaluateProductRequest;
import restaurant.application.dto.Product.TopRatedProductResponse;
import restaurant.domain.model.ProductEvaluation;
import restaurant.infrastructure.Document.MongoProductEvaluation;

@Component
public class ProductEvaluationMapper {

    public MongoProductEvaluation toDocument(ProductEvaluation eval) {
        return new MongoProductEvaluation(
                eval.getId(),
                eval.getProductId(),
                eval.getClientId(),
                eval.getRating(),
                eval.getComment(),
                eval.getCreatedAt()
        );
    }

    // Convert MongoProductEvaluation to ProductEvaluation
    public ProductEvaluation toDomain(MongoProductEvaluation doc) {
        return ProductEvaluation.builder()
                .id(doc.getId())
                .productId(doc.getProductId())
                .clientId(doc.getClientId())
                .rating(doc.getRating())
                .comment(doc.getComment())
                .createdAt(doc.getCreatedAt())
                .build();
    }

    // Convert EvaluateProductRequest to ProductEvaluation (Create new evaluation)
    public ProductEvaluation toDomain(EvaluateProductRequest dto, String productId, String clientId) {
        return ProductEvaluation.builder()
                .productId(productId)
                .clientId(clientId)
                .rating(dto.rating())
                .comment(dto.comment()) // Comment may be optional
                .build();
    }

    // Convert TopRatedProductResponse to ProductEvaluation (For averaging ratings)
    public ProductEvaluation toDomain(TopRatedProductResponse dto) {
        return ProductEvaluation.builder()
                .productId(dto.productId())
                .rating((int) dto.averageRating())
                .build();
    }
}
