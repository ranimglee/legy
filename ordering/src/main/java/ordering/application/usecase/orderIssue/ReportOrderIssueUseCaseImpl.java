package ordering.application.usecase.orderIssue;

import lombok.extern.slf4j.Slf4j;
import ordering.application.dto.order.ReportIssueRequestDTO;
import ordering.application.dto.order.ReportIssueResponseDTO;
import ordering.application.dto.orderIssue.OrderIssueNotification;
import ordering.application.exception.ForbiddenOrderAccessException;
import ordering.application.exception.OrderNotFoundException;
import ordering.domain.model.*;
import ordering.domain.repository.ModeratorTokenRepository;
import ordering.domain.repository.OrderIssueRepository;
import ordering.domain.repository.OrderRepository;
import ordering.notification.FCMService;
import ordering.notification.RedisNotificationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class ReportOrderIssueUseCaseImpl implements ReportOrderIssueUseCase {

    private final OrderRepository orderRepository;
    private final OrderIssueRepository issueRepository;
    private final FCMService fcmService;
    private final ModeratorTokenRepository tokenRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisNotificationService redisNotificationService;
    @Qualifier("moderatorRedisTemplate")
    private final RedisTemplate<String, OrderIssueNotification> redisTemplate;

    public ReportOrderIssueUseCaseImpl(OrderRepository orderRepository, OrderIssueRepository issueRepository, FCMService fcmService, ModeratorTokenRepository tokenRepository, SimpMessagingTemplate messagingTemplate, RedisNotificationService redisNotificationService, RedisTemplate<String, OrderIssueNotification> redisTemplate) {
        this.orderRepository = orderRepository;
        this.issueRepository = issueRepository;
        this.fcmService = fcmService;
        this.tokenRepository = tokenRepository;
        this.messagingTemplate = messagingTemplate;
        this.redisNotificationService = redisNotificationService;
        this.redisTemplate = redisTemplate;
    }
    private static final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public ReportIssueResponseDTO handle(ReportIssueRequestDTO request, String userId) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new OrderNotFoundException(request.orderId()));

        if (!order.getClient().getClientId().equals(userId)) {
            throw new ForbiddenOrderAccessException(userId, request.orderId());
        }

        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Issues can only be reported for delivered orders.");
        }

        String issueRef = generateUniqueIssueRef();

        OrderIssue issue = OrderIssue.builder()
                .id(UUID.randomUUID().toString())
                .orderId(order.getId())
                .userId(userId)
                .type(request.type())
                .issueRef(issueRef)
                .description(request.description())
                .attachments(request.attachmentUrls())
                .status(IssueResolutionStatus.PENDING)
                .reportedAt(Instant.now())
                .build();

        long issueCount = issueRepository.countByOrderId(request.orderId());
        if (issueCount >= 6) {
            throw new IllegalStateException("Maximum issue limit reached for this order");
        }
        issueRepository.save(issue);
        List<ModeratorToken> moderatorTokens = tokenRepository.findAll();

        String title = "Order Issue Reported";
        String body = String.format(
                "An order issue got reported with Ref: %s by %s at %s",
                issueRef,
                order.getClient().getFirstName() + " " + order.getClient().getLastName(),
                LocalDateTime.now()
        );

        if (!moderatorTokens.isEmpty()) {
            for (ModeratorToken token : moderatorTokens) {
                try {
                    String cleanToken = token.getFcmToken(); // Use the token directly
                    log.info("Sending FCM notification to moderator with token {}", cleanToken);
                    fcmService.sendToModerator(cleanToken, title, body);
                } catch (Exception e) {
                    log.error("Failed to send FCM notification to moderator: {}", e.getMessage(), e);
                }
            }


            // Send WebSocket notification to all subscribed moderators
            OrderIssueNotification notification = new OrderIssueNotification(
                    issueRef,
                    title,
                    body,
                    userId,
                    order.getClient().getFirstName() + " " + order.getClient().getLastName()
            );

            log.info("Broadcasting WebSocket notification for issueRef: {}", issueRef);
            messagingTemplate.convertAndSend("/topic/order-issues", notification);
            // Save to recent notifications list
            String listKey = "moderator:notifications:recent";
            redisTemplate.opsForList().leftPush(listKey, notification);
            redisTemplate.expire(listKey, Duration.ofDays(1));
            redisNotificationService.saveNotification(notification);

            log.info("Notification stored in Redis with key: moderator:notifications:{}", issueRef);
        } else {
            log.warn("No moderator tokens found. Notification not sent for issueRef {}", issueRef);
        }

        return new ReportIssueResponseDTO(
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
    }


    private String generateUniqueIssueRef() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String issueRef;

        do {
            StringBuilder code = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                int randomIndex = secureRandom.nextInt(chars.length());
                code.append(chars.charAt(randomIndex));
            }
            issueRef = "REC" + code.toString();
        } while (issueRepository.existsByIssueRef(issueRef));

        return issueRef;
    }

}

