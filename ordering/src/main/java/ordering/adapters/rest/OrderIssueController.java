package ordering.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ordering.application.dto.order.ReportIssueRequestDTO;
import ordering.application.dto.order.ReportIssueResponseDTO;
import ordering.application.dto.orderIssue.OrderIssueSummaryDTO;
import ordering.application.dto.orderIssue.UploadResponseDTO;
import ordering.application.usecase.orderIssue.ReportOrderIssueUseCase;
import ordering.application.usecase.orderIssue.GetMyOrderIssuesUseCase;
import ordering.domain.model.IssueResolutionStatus;
import ordering.domain.model.IssueType;
import ordering.infrastructure.s3.S3UploadService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shared.config.security.JwtUtil;


import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order Issues", description = "Endpoint for reporting issues related to placed orders")
public class OrderIssueController {

    private final ReportOrderIssueUseCase reportUseCase;
    private final JwtUtil jwtUtil;
    private final S3UploadService s3UploadService;
    private final GetMyOrderIssuesUseCase getMyOrderIssuesUseCase;


    @Operation(
            summary = "Report an issue with an order",
            description = "Allows a user to report a problem with an order, such as missing items or delivery issues. Requires authentication."
    )
    @PostMapping("/report-issue")
    public ResponseEntity<ReportIssueResponseDTO> reportIssue(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ReportIssueRequestDTO request
    ) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.extractUserIdFromAccessToken(token);
        ReportIssueResponseDTO response = reportUseCase.handle(request, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Upload attachment",
            description = "Uploads an image file to S3 and returns its public URL"
    )
    @PostMapping(value = "/upload-attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponseDTO> uploadAttachment(
            @RequestParam("file") MultipartFile file) {
        String url = s3UploadService.uploadFile(file);
        return ResponseEntity.ok(new UploadResponseDTO(url));
    }

    @Operation(
            summary = "Get my reported issues with filters",
            description = "Returns a list of reported issues with optional filters like status, type, and date range."
    )
    @GetMapping("/issues/me")
    public ResponseEntity<List<OrderIssueSummaryDTO>> getMyIssues(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) IssueResolutionStatus status,
            @RequestParam(required = false) IssueType type,
            @RequestParam(required = false) String from, // ISO 8601 format
            @RequestParam(required = false) String to
    ) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.extractUserIdFromAccessToken(token);
        List<OrderIssueSummaryDTO> issues = getMyOrderIssuesUseCase.handle(userId, status, type, from, to);
        return ResponseEntity.ok(issues);
    }



}
