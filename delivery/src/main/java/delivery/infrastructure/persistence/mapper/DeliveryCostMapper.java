package delivery.infrastructure.persistence.mapper;

import delivery.domain.model.DeliveryCost;
import delivery.infrastructure.persistence.document.DeliveryCostDocument;

public class DeliveryCostMapper {
    private DeliveryCostMapper() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }
    public static DeliveryCost toDomain(DeliveryCostDocument doc) {
        if (doc == null) return null;
        return new DeliveryCost(
                doc.getId(),
                doc.getDistanceKm(),
                doc.getCostPerKm(),
                doc.getTotalCost(),
                doc.getOrderId(),
                doc.getCreatedAt()
        );
    }

    public static DeliveryCostDocument toDocument(DeliveryCost domain) {
        if (domain == null) return null;
        return new DeliveryCostDocument(
                domain.getId(),
                domain.getDistanceKm(),
                domain.getCostPerKm(),
                domain.getTotalCost(),
                domain.getOrderId(),
                domain.getCreatedAt()
        );
    }
}
