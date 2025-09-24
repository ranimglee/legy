package ordering.application.dto.order;

import java.util.List;

public record OrderItemDTO(
        String productId,
        String productName,
        String productImage,
        Double unitPrice,
        Integer quantity,
        Double promotionAmount,
        List<SupplementSelectionDTO> selectedSupplements
) {}

