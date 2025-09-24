package ordering.infrastructure.mapper;

import ordering.domain.model.PayoutConfigEntity;
import ordering.infrastructure.Document.PayoutConfigDocument;
import org.springframework.stereotype.Component;

@Component

public class PayoutConfigMapper {

    public static PayoutConfigEntity toEntity(PayoutConfigDocument doc) {
        if (doc == null) return null;

        return PayoutConfigEntity.builder()
                .id(doc.getId())
                .bonusAmount(doc.getBonusAmount())
                .bonusThreshold(doc.getBonusThreshold())
                .penaltyPerRefusal(doc.getPenaltyPerRefusal())
                .baseDeliveryFee(doc.getBaseDeliveryFee())
                .driverCostPerKm(doc.getDriverCostPerKm())
                .weatherFee(doc.getWeatherFee())
                .build();
    }

    public static PayoutConfigDocument toDocument(PayoutConfigEntity entity) {
        if (entity == null) return null;

        return PayoutConfigDocument.builder()
                .id(entity.getId())
                .bonusAmount(entity.getBonusAmount())
                .bonusThreshold(entity.getBonusThreshold())
                .penaltyPerRefusal(entity.getPenaltyPerRefusal())
                .baseDeliveryFee(entity.getBaseDeliveryFee())
                .driverCostPerKm(entity.getDriverCostPerKm())
                .weatherFee(entity.getWeatherFee())
                .build();
    }
}
