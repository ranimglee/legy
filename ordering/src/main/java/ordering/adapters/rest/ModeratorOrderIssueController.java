package ordering.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import ordering.application.dto.order.OrderIssueFilterRequest;
import ordering.application.dto.order.OrderIssueWithDetailsDTO;
import ordering.application.dto.order.ReportIssueResponseDTO;
import ordering.application.dto.orderIssue.OrderIssueNotification;
import ordering.domain.model.IssueResolutionStatus;
import ordering.domain.model.OrderIssue;
import ordering.domain.service.OrderIssuesService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import shared.config.security.JwtUtil;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/moderateur/issues")
@Tag(name = "Moderator Order Issues", description = "Endpoints for moderators to manage reported issues")
public class ModeratorOrderIssueController {

    private final OrderIssuesService orderIssuesService;
    private final JwtUtil jwtUtil;
    @Qualifier("moderatorRedisTemplate")
    private final RedisTemplate<String, OrderIssueNotification> redisTemplate;

    public ModeratorOrderIssueController(
            OrderIssuesService orderIssuesService,
            JwtUtil jwtUtil,
            @Qualifier("moderatorRedisTemplate") RedisTemplate<String, OrderIssueNotification> redisTemplate
    ) {
        this.orderIssuesService = orderIssuesService;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/get-all-order-issues")
    @Operation(summary = "Get all reported issues")
    public ResponseEntity<List<OrderIssue>> getAllIssues(
    ) {
        return ResponseEntity.ok(orderIssuesService.getAllIssues());
    }


    @GetMapping("/get-order-issue-by-id/{id}")
    @Operation(summary = "Get a specific issue by ID")
    public ResponseEntity<OrderIssueWithDetailsDTO> getIssueById(@PathVariable String id) {
        Optional<OrderIssueWithDetailsDTO> issueOpt = orderIssuesService.getIssueById(id);
        return issueOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/update-order-issue-status-by/{id}")
    @Operation(summary = "Update the resolution status of an issue")
    public ResponseEntity<ReportIssueResponseDTO> updateIssueStatus(
            @PathVariable String id,
            @RequestParam IssueResolutionStatus newStatus,
            HttpServletRequest request) {

        // Extract the Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Missing or invalid Authorization header"
            );
        }

        // Remove "Bearer " prefix
        String token = authHeader.substring(7);

        // Extract moderator ID from token
        String moderatorId = jwtUtil.extractUserIdFromAccessToken(token);

        // Call service with moderator ID
        ReportIssueResponseDTO updatedIssue = orderIssuesService.updateIssueStatus(id, newStatus, moderatorId);

        return ResponseEntity.ok(updatedIssue);
    }

    @GetMapping("/get-filtered-order-issues")
    @Operation(summary = "Get issues with filters and pagination")
    public ResponseEntity<List<OrderIssue>> getFilteredIssues(
            @RequestParam(required = false) IssueResolutionStatus status,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        OrderIssueFilterRequest filter = OrderIssueFilterRequest.builder()
                .status(status)
                .severity(severity)
                .startDate(startDate)
                .endDate(endDate)
                .page(page)
                .size(size)
                .build();

        return ResponseEntity.ok(orderIssuesService.getFilteredIssues(filter));
    }


    @GetMapping("/notifications/{issueRef}")
    public ResponseEntity<OrderIssueNotification> getNotification(@PathVariable String issueRef) {
        OrderIssueNotification notification = redisTemplate.opsForValue().get("moderator:notifications:" + issueRef);
        if (notification == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(notification);
    }


}
