package ordering.application.dto.order;

public record SupplementSelectionDTO(
        String supplementId,
        String supplementName,
        int quantity
) {}
