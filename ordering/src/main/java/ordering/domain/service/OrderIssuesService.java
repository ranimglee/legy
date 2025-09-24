package ordering.domain.service;

import lombok.extern.slf4j.Slf4j;
import ordering.application.dto.order.*;
import ordering.application.dto.orderIssue.ModeratorNotificationDTO;
import ordering.domain.model.IssueResolutionStatus;
import ordering.domain.model.Order;
import ordering.domain.model.OrderIssue;
import ordering.domain.model.value.DeliveryInfo;
import ordering.domain.repository.OrderIssueRepository;
import ordering.domain.repository.OrderRepository;
import ordering.domain.repository.UserTokenRepository;
import ordering.infrastructure.Document.IssueMonthlyStats;
import ordering.infrastructure.persistence.IssueMonthlyStatsRepository;
import ordering.notification.RedisNotificationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ordering.notification.FCMService;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderIssuesService {

    private final OrderIssueRepository issueRepository;
    private final IssueMonthlyStatsRepository monthlyStatsRepository;
    @Qualifier("issueStatsRedisTemplate")
    private final RedisTemplate<String, IssueMonthlyStats> redisTemplate;
    private final OrderRepository orderRepository;
    private final UserTokenRepository userTokenRepository;
    private final FCMService fcmService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisNotificationService redisNotificationService;
    @Qualifier("moderatorNotificationRedisTemplate")
    private final RedisTemplate<String, ModeratorNotificationDTO> redisModeratorTemplate;
    public OrderIssuesService(OrderIssueRepository issueRepository, IssueMonthlyStatsRepository monthlyStatsRepository, RedisTemplate<String, IssueMonthlyStats> redisTemplate, OrderRepository orderRepository, UserTokenRepository userTokenRepository, FCMService fcmService, SimpMessagingTemplate messagingTemplate, RedisNotificationService redisNotificationService, RedisTemplate<String, ModeratorNotificationDTO> redisModeratorTemplate) {
        this.issueRepository = issueRepository;
        this.monthlyStatsRepository = monthlyStatsRepository;
        this.redisTemplate = redisTemplate;
        this.orderRepository = orderRepository;
        this.userTokenRepository = userTokenRepository;
        this.fcmService = fcmService;
        this.messagingTemplate = messagingTemplate;
        this.redisNotificationService = redisNotificationService;
        this.redisModeratorTemplate = redisModeratorTemplate;
    }

    public List<OrderIssue> getAllIssues() {
        return issueRepository.findAll();
    }

    public List<OrderIssue> getFilteredIssues(OrderIssueFilterRequest filter) {
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), Sort.by("reportedAt").descending());
        List<OrderIssue> all = issueRepository.findAll();

        return all.stream()
                .filter(issue -> filter.getStatus() == null || issue.getStatus() == filter.getStatus())
                .filter(issue -> filter.getSeverity() == null || issue.getSeverity().equalsIgnoreCase(filter.getSeverity()))
                .filter(issue -> {
                    if (filter.getStartDate() == null && filter.getEndDate() == null) return true;
                    Instant reportedAt = issue.getReportedAt();
                    boolean afterStart = filter.getStartDate() == null || !reportedAt.isBefore(filter.getStartDate());
                    boolean beforeEnd = filter.getEndDate() == null || !reportedAt.isAfter(filter.getEndDate());
                    return afterStart && beforeEnd;
                })
                .sorted(Comparator.comparing(OrderIssue::getReportedAt).reversed())
                .skip((long) filter.getPage() * filter.getSize())
                .limit(filter.getSize())
                .toList();
    }
    public Optional<OrderIssueWithDetailsDTO> getIssueById(String id) {
        Optional<OrderIssue> orderIssueOpt = issueRepository.findById(id);
        if (orderIssueOpt.isEmpty()) {
            return Optional.empty();
        }

        OrderIssue orderIssue = orderIssueOpt.get();
        Optional<Order> orderOpt = orderRepository.findById(orderIssue.getOrderId());
        if (orderOpt.isEmpty()) {
            return Optional.empty();
        }

        Order order = orderOpt.get();
        return Optional.of(mapToOrderIssueWithDetailsDTO(orderIssue, order));
    }

    private OrderIssueWithDetailsDTO mapToOrderIssueWithDetailsDTO(OrderIssue issue, Order order) {
        ReportIssueResponseDTO issueDTO = new ReportIssueResponseDTO(
                issue.getId(),
                issue.getOrderId(),
                issue.getUserId(),
                issue.getType(),
                issue.getDescription(),
                issue.getReportedAt(),
                issue.getAttachments(),
                issue.getStatus(),
                issue.getResolvedAt(),
                issue.getResolvedBy(),
                issue.getIssueRef()
        );

        ClientInfoDTO clientDTO = new ClientInfoDTO(
                order.getClient().getClientId(),
                order.getClient().getFirstName(),
                order.getClient().getLastName(),
                order.getClient().getPhone(),
                order.getClient().getAddress(),
                order.getClient().getLongitude(),
                order.getClient().getLatitude()
        );

        RestaurantDetailsDTO restaurantDTO = new RestaurantDetailsDTO(
                order.getRestaurant().getRestaurantId(),
                order.getRestaurant().getName(),
                order.getRestaurant().getPhone(),
                order.getRestaurant().getAddress()
        );

        DeliveryInfo driverDTO = order.getDeliveryInfo() != null ? new DeliveryInfo(
                order.getDeliveryInfo().getDeliveryPersonId(),
                order.getDeliveryInfo().getDeliveryPersonName(),
                order.getDeliveryInfo().getContactNumber(),
                order.getDeliveryInfo().getVehicleInfo()
        ) : null;

        OrderInfoDTO orderDTO = new OrderInfoDTO(
                order.getId(),
                order.getItems(),
                order.getTotal(),
                order.getOrderStatus(),
                order.getDeliveryAddress(),
                clientDTO,
                restaurantDTO,
                driverDTO
        );

        return new OrderIssueWithDetailsDTO(issueDTO, orderDTO);
    }
    public ReportIssueResponseDTO updateIssueStatus(String id, IssueResolutionStatus newStatus, String resolvedById) {
        log.debug("Updating issue status for issueId: {}, newStatus: {}, resolvedById: {}", id, newStatus, resolvedById);

        OrderIssue issue = issueRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Order issue not found for id: {}", id);
                    return new RuntimeException("Order issue not found");
                });

        issue.setStatus(newStatus);
        issue.setResolvedAt(newStatus == IssueResolutionStatus.RESOLVED ? Instant.now() : null);
        issue.setResolvedBy(resolvedById);

        OrderIssue updated = issueRepository.save(issue);
        log.info("Issue status updated successfully for issueId: {}, newStatus: {}", id, newStatus);

        // Send notification to the client
        String userId = issue.getUserId();
        log.debug("Looking up FCM token for userId: {}", userId);
        userTokenRepository.findFCMTokenByUserId(userId).ifPresentOrElse(
                fcmToken -> {
                    String cleanedToken = cleanToken(fcmToken);
                    log.debug("Validating FCM token for userId: {}, token: {}", userId, cleanedToken);
                    if (isValidFcmToken(cleanedToken)) {
                        String title = "Order Issue Status Updated";
                        String body = String.format(
                                "The status of your issue (Ref: %s) has been updated to %s.",
                                issue.getIssueRef(),
                                newStatus
                        );
                        log.debug("Sending FCM notification to userId: {}, token: {}", userId, cleanedToken);
                        fcmService.sendToClient(cleanedToken, title, body);
                        log.info("FCM notification sent successfully to userId: {} for issueId: {}", userId, id);
                    } else {
                        log.warn("Invalid FCM token format for userId: {}. Token: {}", userId, cleanedToken);
                        userTokenRepository.findByFcmToken(cleanedToken)
                                .ifPresent(doc -> userTokenRepository.delete(doc.getId()));
                    }
                },
                () -> log.warn("No FCM token found for userId: {} for issueId: {}", userId, id)
        );
        ModeratorNotificationDTO notification = new ModeratorNotificationDTO(
                "IssueStatusUpdated",
                String.format("Issue %s has been updated to %s", updated.getIssueRef(), newStatus),
                Instant.now()
        );


        messagingTemplate.convertAndSend("/topic/moderator/order-issue", notification);

// Save to recent notifications list
        String listKey = "moderator:notifications:recent";
        redisModeratorTemplate.opsForList().leftPush(listKey, notification);
        redisModeratorTemplate.expire(listKey, Duration.ofDays(1));

// Optionally still save the individual notification as before
        redisNotificationService.saveModeratorNotification(notification);

        log.info("WebSocket notification sent to /topic/moderator/order-issue");
        return new ReportIssueResponseDTO(
                updated.getId(),
                updated.getOrderId(),
                updated.getUserId(),
                updated.getType(),
                updated.getDescription(),
                updated.getReportedAt(),
                updated.getAttachments(),
                updated.getStatus(),
                updated.getResolvedAt(),
                updated.getResolvedBy(),
                updated.getIssueRef()
        );
    }

    private String cleanToken(String fcmToken) {
        try {
            if (fcmToken != null && fcmToken.startsWith("{\"token\":\"") && fcmToken.endsWith("\"}")) {
                log.warn("Cleaning JSON-wrapped FCM token: {}", fcmToken);
                return fcmToken.substring(10, fcmToken.length() - 2);
            }
            return fcmToken != null ? fcmToken : "";
        } catch (Exception e) {
            log.error("Failed to clean FCM token: {}, error: {}", fcmToken, e.getMessage(), e);
            return fcmToken != null ? fcmToken : "";
        }
    }

    private boolean isValidFcmToken(String token) {
        // Basic validation: FCM tokens are typically long strings with colons
        return token != null && token.length() > 50 && token.contains(":") && !token.startsWith("{");
    }



    public ResolutionRateStats getResolutionRate() {
        List<OrderIssue> all = issueRepository.findAll();
        List<OrderIssue> lastMonth = all.stream()
                .filter(i -> i.getReportedAt().isAfter(Instant.now().minus(30, ChronoUnit.DAYS)))
                .toList();

        double totalRate = computeResolutionRate(all);
        double monthRate = computeResolutionRate(lastMonth);

        return new ResolutionRateStats(totalRate, monthRate - totalRate);
    }

    public AvgResponseTimeStats getAvgResponseTime() {
        List<OrderIssue> resolved = issueRepository.findAll().stream()
                .filter(i -> i.getStatus() == IssueResolutionStatus.RESOLVED)
                .filter(i -> i.getReportedAt() != null && i.getResolvedAt() != null)
                .toList();

        double avgAll = computeAvgResponseTime(resolved);

        double avgMonth = computeAvgResponseTime(resolved.stream()
                .filter(i -> i.getReportedAt().isAfter(Instant.now().minus(30, ChronoUnit.DAYS)))
                .toList());

        return new AvgResponseTimeStats(avgAll, avgMonth - avgAll);
    }

    public Map<String, Long> getHighPriorityStats() {
        List<OrderIssue> issues = issueRepository.findAll();

        long urgent = issues.stream().filter(i -> "Urgent".equalsIgnoreCase(i.getSeverity())).count();
        long high = issues.stream().filter(i -> "Élevé".equalsIgnoreCase(i.getSeverity()) || "High".equalsIgnoreCase(i.getSeverity())).count();

        return Map.of("urgent", urgent, "high", high);
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void refreshMonthlyStats() {
        YearMonth current = YearMonth.now();
        YearMonth previous = current.minusMonths(1);
        String redisKey = "monthly-stats:" + current;

        List<OrderIssue> currentIssues = findIssuesByMonth(current);
        List<OrderIssue> previousIssues = findIssuesByMonth(previous);

        double avgCurrent = computeAvgResponseTime(currentIssues);
        double avgPrevious = computeAvgResponseTime(previousIssues);
        double rateCurrent = computeResolutionRate(currentIssues);
        double ratePrevious = computeResolutionRate(previousIssues);

        IssueMonthlyStats stats = new IssueMonthlyStats();
        stats.setMonth(current.toString());
        stats.setAvgResponseTime(avgCurrent);
        stats.setResolutionRate(rateCurrent);
        stats.setDeltaResponseTime(avgCurrent - avgPrevious);
        stats.setDeltaResolutionRate(rateCurrent - ratePrevious);

        monthlyStatsRepository.save(stats);
        redisTemplate.opsForValue().set(redisKey, stats, Duration.ofDays(1));
    }

    public Optional<IssueMonthlyStats> getCachedStats(String month) {
        return Optional.ofNullable(redisTemplate.opsForValue().get("monthly-stats:" + month));
    }

    // --- Helper methods ---

    private List<OrderIssue> findIssuesByMonth(YearMonth month) {
        Instant start = month.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant end = month.plusMonths(1).atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        return issueRepository.findAll().stream()
                .filter(i -> i.getReportedAt() != null && !i.getReportedAt().isBefore(start) && i.getReportedAt().isBefore(end))
                .toList();
    }

    private double computeAvgResponseTime(List<OrderIssue> issues) {
        return issues.stream()
                .filter(i -> i.getResolvedAt() != null && i.getReportedAt() != null)
                .mapToLong(i -> Duration.between(i.getReportedAt(), i.getResolvedAt()).toMinutes())
                .average()
                .orElse(0.0) / 60.0;
    }

    private double computeResolutionRate(List<OrderIssue> issues) {
        long resolved = issues.stream().filter(i -> i.getStatus() == IssueResolutionStatus.RESOLVED).count();
        return issues.isEmpty() ? 0 : (resolved * 100.0 / issues.size());
    }

    public Map<String, Long> getIssueSummaryStats() {
        List<OrderIssue> issues = issueRepository.findAll();

        long total = issues.size();
        long pending = issues.stream().filter(i -> i.getStatus() == IssueResolutionStatus.PENDING).count();
        long resolved = issues.stream().filter(i -> i.getStatus() == IssueResolutionStatus.RESOLVED).count();
        long rejected = issues.stream().filter(i -> i.getStatus() == IssueResolutionStatus.REJECTED).count();

        return Map.of(
                "total", total,
                "pending", pending,
                "resolved", resolved,
                "rejected", rejected
        );
    }
    public List<ModeratorStatsDTO> getModeratorStats(Instant startDate, Instant endDate) {
        List<OrderIssue> issues = issueRepository.findAll().stream()
                .filter(i -> i.getResolvedAt() != null)
                .filter(i -> i.getResolvedAt().isAfter(startDate) && i.getResolvedAt().isBefore(endDate))
                .toList();

        Map<String, List<OrderIssue>> grouped = issues.stream()
                .collect(Collectors.groupingBy(i ->
                        i.getResolvedBy() != null ? i.getResolvedBy() : "Unknown"));

        return grouped.entrySet().stream().map(entry -> {
            List<OrderIssue> modIssues = entry.getValue();

            // Compute average time
            double avgTime = modIssues.stream()
                    .mapToLong(i -> Duration.between(i.getReportedAt(), i.getResolvedAt()).toMinutes())
                    .average().orElse(0.0) / 60.0;

            // ✅ Compute resolution rate properly
            long resolvedCount = modIssues.stream()
                    .filter(i -> i.getStatus() == IssueResolutionStatus.RESOLVED)
                    .count();

            double resolutionRate = modIssues.isEmpty() ? 0.0 : (resolvedCount * 100.0 / modIssues.size());

            return new ModeratorStatsDTO(
                    entry.getKey(),
                    modIssues.size(),
                    (int) resolvedCount,
                    avgTime,
                    resolutionRate
            );

        }).toList();
    }

    public Map<String, Long> getIssueTypeDistribution() {
        List<OrderIssue> issues = issueRepository.findAll();

        return issues.stream()
                .filter(issue -> issue.getType() != null)
                .collect(Collectors.groupingBy(
                        issue -> issue.getType().name(),
                        Collectors.counting()
                ));
    }

}
