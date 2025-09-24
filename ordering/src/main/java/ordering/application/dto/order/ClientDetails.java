package ordering.application.dto.order;

import ordering.domain.model.value.ClientInfo;
import ordering.infrastructure.Document.OrderDocument;

import java.util.List;

public record ClientDetails(ClientInfo clientInfo, List<OrderDocument> orderHistory) {}
