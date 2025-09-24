package restaurant.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import restaurant.domain.model.RestaurantReaction;
import restaurant.domain.repository.RestaurantReactionRepository;
import restaurant.infrastructure.Document.MongoRestaurantReaction;
import restaurant.infrastructure.mapper.RestaurantReactionMapper;

import javax.swing.text.Document;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class RestaurantReactionRepositoryImpl implements RestaurantReactionRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<RestaurantReaction> findByUserAndRestaurant(String userId, String restaurantId) {
        var query = new org.springframework.data.mongodb.core.query.Query()
                .addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("userId").is(userId)
                        .and("restaurantId").is(restaurantId));
        var result = mongoTemplate.findOne(query, MongoRestaurantReaction.class);
        return Optional.ofNullable(result).map(RestaurantReactionMapper::toDomain);
    }

    @Override
    public void save(RestaurantReaction reaction) {
        mongoTemplate.save(RestaurantReactionMapper.toMongo(reaction));
    }

    @Override
    public void delete(RestaurantReaction reaction) {
        var query = new org.springframework.data.mongodb.core.query.Query()
                .addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("userId").is(reaction.getUserId())
                        .and("restaurantId").is(reaction.getRestaurantId()));
        mongoTemplate.remove(query, MongoRestaurantReaction.class);
    }

    @Override
    public long countByRestaurantAndReaction(String restaurantId, RestaurantReaction.ReactionType type) {
        var query = new org.springframework.data.mongodb.core.query.Query()
                .addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("restaurantId").is(restaurantId)
                        .and("reaction").is(type));
        return mongoTemplate.count(query, MongoRestaurantReaction.class);
    }


}
