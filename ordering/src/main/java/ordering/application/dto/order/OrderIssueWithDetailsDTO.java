package ordering.application.dto.order;

public record OrderIssueWithDetailsDTO(
        ReportIssueResponseDTO issue,
        OrderInfoDTO order
) {}