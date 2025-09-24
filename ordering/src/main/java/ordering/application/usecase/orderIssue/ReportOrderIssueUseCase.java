package ordering.application.usecase.orderIssue;

import ordering.application.dto.order.ReportIssueRequestDTO;
import ordering.application.dto.order.ReportIssueResponseDTO;

public interface ReportOrderIssueUseCase {
    /**
     * @param request the problem report payload (orderId, type, description)
     * @param userId  the id of the reporting user, extracted from JWT
     */
    ReportIssueResponseDTO handle(ReportIssueRequestDTO request, String userId);
}
