package ordering.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ordering.application.dto.order.AvgResponseTimeStats;
import ordering.application.dto.order.ModeratorStatsDTO;
import ordering.application.dto.order.ResolutionRateStats;
import ordering.domain.service.OrderIssuesService;
import ordering.application.utils.PdfExportOrderIssueService;
import ordering.infrastructure.Document.IssueMonthlyStats;
import ordering.infrastructure.persistence.IssueMonthlyStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/moderateur/issues/stats")
@RequiredArgsConstructor
@Tag(name = "Moderator Issue Statistics", description = "Endpoints to monitor issue KPIs for moderators")
public class ModeratorIssueStatsController {
    private static final Logger logger = LoggerFactory.getLogger(ModeratorIssueStatsController.class);

    private final OrderIssuesService service;
    private final IssueMonthlyStatsRepository monthlyStatsRepository;
    private final PdfExportOrderIssueService pdfExportOrderIssueService;


    @GetMapping("/summary")
    @Operation(summary = "Get summary counts of issues (total, pending, resolved)")
    public ResponseEntity<Map<String, Long>> getIssueSummary() {
        return ResponseEntity.ok(service.getIssueSummaryStats());

    }

    @GetMapping("/resolution-rate")
    @Operation(summary = "Get resolution rate and monthly variation")
    public ResponseEntity<ResolutionRateStats> getResolutionRate(
    ) {
        return ResponseEntity.ok(service.getResolutionRate());
    }

    @GetMapping("/avg-response-time")
    @Operation(summary = "Get average response time and delta this month")
    public ResponseEntity<AvgResponseTimeStats> getAvgResponseTime(
    ) {
        return ResponseEntity.ok(service.getAvgResponseTime());
    }

    @GetMapping("/high-priority")
    @Operation(summary = "Get high-priority issue counts: urgent, elevated")
    public ResponseEntity<Map<String, Long>> getHighPriorityStats(
    ) {
        return ResponseEntity.ok(service.getHighPriorityStats());
    }

    @GetMapping("/monthly")
    @Operation(summary = "Get cached monthly issue stats from Redis or fallback to Mongo")
    public ResponseEntity<IssueMonthlyStats> getMonthlyStats(
            @RequestParam(defaultValue = "#{T(java.time.YearMonth).now().toString()}") String month
    )
    {
        return service.getCachedStats(month)
                .or(() -> monthlyStatsRepository.findByMonth(month))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/export/issue-stats/pdf")
    public ResponseEntity<byte[]> exportModeratorStatsPdf(@RequestParam String period) {
        try {
            Instant now = Instant.now();
            Instant start = switch (period.toLowerCase()) {
                case "weekly" -> now.minus(7, ChronoUnit.DAYS);
                case "monthly" -> now.minus(30, ChronoUnit.DAYS);
                default -> throw new IllegalArgumentException("Unsupported period");
            };

            List<ModeratorStatsDTO> stats = service.getModeratorStats(start, now);
            byte[] pdf = pdfExportOrderIssueService.exportModeratorStatsToPdf(stats);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=moderator-report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

         } catch (Exception e) {
        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to generate moderator stats PDF for period: " + period, e);
        }
    }
    @GetMapping("/distribution-by-type")
    public ResponseEntity<Map<String, Long>> getIssueTypeDistribution() {
        Map<String, Long> distribution = service.getIssueTypeDistribution();
        return ResponseEntity.ok(distribution);
    }

}
