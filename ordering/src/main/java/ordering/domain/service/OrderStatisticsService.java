package ordering.domain.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ordering.application.dto.order.*;
import ordering.application.exception.LoyaltyRateCalculationException;
import ordering.domain.model.OrderStatus;
import ordering.domain.model.value.ClientInfo;
import ordering.infrastructure.Document.OrderDocument;
import ordering.infrastructure.Document.ReviewDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class OrderStatisticsService {

    private final MongoTemplate mongoTemplate;

    // =========================
    // Constants for Mongo fields & collections
    // =========================
    private static final String CLIENT_ID_FIELD = "client.clientId";
    private static final String STATUS_FIELD = "status";
    private static final String ORDERS_COLLECTION = "orders";
    private static final String REVIEWS_COLLECTION = "reviews";
    private static final String TOTAL_FIELD = "total";
    private static final String TOTAL_AMOUNT_SPENT = "totalAmountSpent";
    private static final String ORDER_COUNT = "orderCount";



    // =========================
    // Methods
    // =========================

    public ClientOrderStats getClientDeliveredOrderStats(String clientId) {
        log.debug("🔍 [getClientDeliveredOrderStats] Calculating delivered orders + total spent for clientId: {}", clientId);

        MatchOperation matchStage = Aggregation.match(
                Criteria.where(CLIENT_ID_FIELD).is(clientId)
                        .and(STATUS_FIELD).is(OrderStatus.DELIVERED.name())
        );

        GroupOperation groupStage = Aggregation.group()
                .count().as("totalOrders")
                .sum(TOTAL_FIELD).as(TOTAL_AMOUNT_SPENT);

        Aggregation aggregation = Aggregation.newAggregation(matchStage, groupStage);

        AggregationResults<ClientStatsAggregationResult> results = mongoTemplate.aggregate(
                aggregation,
                ORDERS_COLLECTION,
                ClientStatsAggregationResult.class
        );

        ClientStatsAggregationResult result = results.getUniqueMappedResult();

        if (result != null) {
            log.debug("✅ [getClientDeliveredOrderStats] Found: totalOrders={}, totalAmountSpent={}",
                    result.getTotalOrders(), result.getTotalAmountSpent());
            return new ClientOrderStats(result.getTotalOrders(), result.getTotalAmountSpent());
        } else {
            log.warn("⚠️ [getClientDeliveredOrderStats] No delivered orders found for clientId: {}", clientId);
            return new ClientOrderStats(0, 0.0);
        }
    }

    public long getTotalClients() {
        log.debug("🔍 [getTotalClients] Counting unique clients");

        long count = mongoTemplate.query(OrderDocument.class)
                .distinct(CLIENT_ID_FIELD)
                .as(String.class)
                .all()
                .size();

        log.debug("✅ [getTotalClients] Total unique clients: {}", count);
        return count;
    }

    public List<ClientOrderStats> getTopBuyers(int limit) {
        log.debug("🔍 [getTopBuyers] Retrieving top {} buyers by total amount spent", limit);

        MatchOperation matchStage = Aggregation.match(Criteria.where(STATUS_FIELD).is(OrderStatus.DELIVERED.name()));
        GroupOperation groupStage = Aggregation.group(CLIENT_ID_FIELD)
                .sum(TOTAL_FIELD).as(TOTAL_AMOUNT_SPENT)
                .count().as("totalOrders");
        SortOperation sortStage = Aggregation.sort(Sort.by(Sort.Direction.DESC, TOTAL_AMOUNT_SPENT));
        LimitOperation limitStage = Aggregation.limit(limit);

        Aggregation aggregation = Aggregation.newAggregation(matchStage, groupStage, sortStage, limitStage);

        AggregationResults<ClientOrderStats> results = mongoTemplate.aggregate(
                aggregation, ORDERS_COLLECTION, ClientOrderStats.class
        );

        List<ClientOrderStats> topBuyers = results.getMappedResults();
        log.debug("✅ [getTopBuyers] Retrieved {} top buyers", topBuyers.size());
        return topBuyers;
    }

    public double getLoyaltyRateFromReviews() {
        try {
            log.debug("🔍 [getLoyaltyRateFromReviews] Fetching reviews from database...");

            List<ReviewDocument> reviews = mongoTemplate.findAll(ReviewDocument.class, REVIEWS_COLLECTION);

            if (reviews == null || reviews.isEmpty()) {
                log.warn("⚠️ [getLoyaltyRateFromReviews] No reviews found in the database.");
                return 0.0;
            }

            log.debug("📦 [getLoyaltyRateFromReviews] Total reviews fetched: {}", reviews.size());

            Map<String, Long> userReviewCounts = reviews.stream()
                    .filter(review -> review.getUser() != null && !review.getUser().isBlank())
                    .collect(Collectors.groupingBy(ReviewDocument::getUser, Collectors.counting()));

            long totalUsers = userReviewCounts.size();

            if (totalUsers == 0) {
                log.warn("⚠️ [getLoyaltyRateFromReviews] No valid users found among the reviews.");
                return 0.0;
            }

            long loyalUsers = userReviewCounts.values().stream()
                    .filter(count -> count >= 3)
                    .count();

            double loyaltyRate = ((double) loyalUsers / totalUsers) * 100;

            log.debug("✅ [getLoyaltyRateFromReviews] Loyalty rate: {}% ({} loyal users out of {})",
                    loyaltyRate, loyalUsers, totalUsers);

            return loyaltyRate;

        } catch (Exception e) {
            log.error("❌ [getLoyaltyRateFromReviews] Error calculating loyalty rate", e);
            throw new LoyaltyRateCalculationException("Failed to calculate loyalty rate from reviews");
        }
    }

    /**
     * Ranks all clients by total number of orders placed, sorted by frequency.
     */
    public List<ClientFrequencyRank> getClientRankingByFrequency() {
        log.debug("🔍 [getClientRankingByFrequency] Ranking clients by total order frequency");

        GroupOperation groupStage = Aggregation.group(CLIENT_ID_FIELD)
                .count().as(ORDER_COUNT);

        // Explicitly project _id to clientId to match the record fields
        ProjectionOperation projectStage = Aggregation.project(ORDER_COUNT)
                .and("_id").as("clientId");

        SortOperation sortStage = Aggregation.sort(Sort.by(Sort.Direction.DESC, ORDER_COUNT));

        Aggregation aggregation = Aggregation.newAggregation(groupStage, projectStage, sortStage);

        AggregationResults<ClientFrequencyRank> results = mongoTemplate.aggregate(
                aggregation, ORDERS_COLLECTION, ClientFrequencyRank.class
        );

        List<ClientFrequencyRank> rankings = results.getMappedResults();
        log.debug("✅ [getClientRankingByFrequency] Retrieved {} ranked clients", rankings.size());
        return rankings;
    }

    public Page<ClientSummary> getClients(String nameFilter, Integer minFreq, Pageable pageable) {
        log.debug("🔍 [getClients] Fetching paginated client list with nameFilter='{}' and minFreq={}",
                nameFilter, minFreq);

        Criteria criteria = new Criteria();
        if (nameFilter != null && !nameFilter.isEmpty()) {
            criteria.and("client.firstName").regex(nameFilter, "i");
        }

        GroupOperation groupStage = Aggregation.group(CLIENT_ID_FIELD, "client.firstName", "client.lastName")
                .count().as(ORDER_COUNT);

        ProjectionOperation projectStage = Aggregation.project(ORDER_COUNT)
                .and("_id.clientId").as("clientId")
                .and("_id.firstName").as("firstName")
                .and("_id.lastName").as("lastName");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                groupStage,
                projectStage,
                Aggregation.match(minFreq != null ? Criteria.where(ORDER_COUNT).gte(minFreq) : new Criteria()),
                Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize()),
                Aggregation.limit(pageable.getPageSize())
        );

        AggregationResults<ClientSummary> results = mongoTemplate.aggregate(
                aggregation, ORDERS_COLLECTION, ClientSummary.class
        );

        List<ClientSummary> clients = results.getMappedResults();
        log.debug("✅ [getClients] Retrieved {} clients on page {}", clients.size(), pageable.getPageNumber());
        return new PageImpl<>(clients, pageable, clients.size());
    }

    /**
     * Retrieves detailed client info and their full order history by client ID.
     */
    public ClientDetails getClientDetails(String clientId) {
        log.debug("🔍 [getClientDetails] Looking for client details with clientId: {}", clientId);

        List<OrderDocument> orders = mongoTemplate.find(
                Query.query(Criteria.where(CLIENT_ID_FIELD).is(clientId)),
                OrderDocument.class,
                ORDERS_COLLECTION
        );

        log.debug("📦 [getClientDetails] Number of orders found: {}", orders.size());

        if (!orders.isEmpty()) {
            log.debug("✅ [getClientDetails] First order found: {}", orders.get(0).getId());
            log.debug("👤 [getClientDetails] Client info from first order: {}", orders.get(0).getClient());
        } else {
            log.warn("⚠️ [getClientDetails] No orders found for this client.");
        }

        ClientInfo clientInfo = orders.isEmpty() ? null : orders.get(0).getClient();

        return new ClientDetails(clientInfo, orders);
    }
    /**
     * Calculates the loyalty rate: percentage of clients with at least 3 orders.
     */
    public double getLoyaltyRate() {
        log.debug("🔍 [getLoyaltyRate] Calculating loyalty rate (clients with >= 3 orders)");

        long totalClients = getTotalClients();

        long loyalClients = mongoTemplate.query(OrderDocument.class)
                .distinct(CLIENT_ID_FIELD)
                .matching(Criteria.where(STATUS_FIELD).is(OrderStatus.DELIVERED.name()))
                .as(String.class)
                .all()
                .stream()
                .filter(clientId -> {
                    long orderCount = mongoTemplate.query(OrderDocument.class)
                            .matching(Criteria.where(CLIENT_ID_FIELD).is(clientId))
                            .count();
                    return orderCount >= 3;
                })
                .count();

        double loyaltyRate = totalClients == 0 ? 0.0 : ((double) loyalClients / totalClients) * 100;
        log.debug("✅ [getLoyaltyRate] Loyalty rate: {}% ({} loyal clients out of {})",
                loyaltyRate, loyalClients, totalClients);
        return loyaltyRate;
    }
}
