package restaurant.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import restaurant.domain.model.ProductEvaluation;
import restaurant.domain.repository.ProductEvaluationRepository;
import restaurant.infrastructure.Document.MongoProduct;
import restaurant.infrastructure.mapper.ProductEvaluationMapper;

import org.bson.Document;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.mongodb.core.MongoTemplate;


@Repository
@RequiredArgsConstructor
public class MongoProductEvaluationRepository implements ProductEvaluationRepository {


    private final SpringDataProductEvalRepo springRepo;
    private final ProductEvaluationMapper mapper;
    private final MongoTemplate mongoTemplate;

    @Override
    public ProductEvaluation save(ProductEvaluation eval) {
        var doc = mapper.toDocument(eval);
        var saved = springRepo.save(doc);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ProductEvaluation> findById(String id) {
        return springRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ProductEvaluation> findByProductId(String productId) {
        return springRepo.findByProductId(productId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductEvaluation> findByProductIdAndClientId(String productId, String clientId) {
        return springRepo.findByProductIdAndClientId(productId, clientId)
                .map(mapper::toDomain);
    }
    @Override
    public Map<String, Double> getAverageRatingsForProducts(List<String> productIds) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("productId").in(productIds)),
                Aggregation.group("productId").avg("rating").as("avgRating")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "product_evaluations", Document.class);

        return results.getMappedResults().stream()
                .collect(Collectors.toMap(
                        doc -> doc.getString("_id"),
                        doc -> doc.getDouble("avgRating")
                ));
    }

    @Override
    public Map<String, Integer> getEvaluationCountsForProducts(List<String> productIds) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("productId").in(productIds)),
                Aggregation.group("productId").count().as("count")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "product_evaluations", Document.class);

        return results.getMappedResults().stream()
                .collect(Collectors.toMap(
                        doc -> doc.getString("_id"),
                        doc -> doc.getInteger("count")
                ));
    }
    @Override
    public void updateRatingAndCount(String productId, double averageRating, int reviewCount) {
        Query query = Query.query(Criteria.where("_id").is(productId));
        Update update = new Update()
                .set("averageRating", averageRating)
                .set("reviewCount", reviewCount);
        mongoTemplate.updateFirst(query, update, MongoProduct.class);
    }

}
