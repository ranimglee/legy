package ordering.application.dto.order;

import ordering.domain.model.DeliveryMode;

public record CommissionUpdateDTO(
        double deliveryCommissionPercentage,
        double pickupCommissionPercentage,
        String modifiedBy,
        DeliveryMode deliveryMode)
{


}
