package ordering.application.mapper;

import ordering.application.dto.order.OrderDetailsDTO;
import ordering.application.dto.order.OrderItemDTO;
import ordering.application.dto.order.SupplementSelectionDTO;
import ordering.domain.model.Order;

import java.util.List;
import java.util.stream.Collectors;

public class OrderDetailsMapper {

    public static OrderDetailsDTO toDto(Order order) {
        List<OrderItemDTO> items = order.getItems().stream().map(item -> {
            List<SupplementSelectionDTO> supplements = item.getSupplements() == null ? List.of() :
                    item.getSupplements().stream()
                            .map(s -> new SupplementSelectionDTO(s.getSupplementId(), s.getSupplementName(), s.getQuantity()))
                            .collect(Collectors.toList());

            return new OrderItemDTO(
                    item.getProductId(),
                    item.getProductName(),
                    item.getProductImageUrl(),
                    item.getUnitPrice(),
                    item.getQuantity(),
                    item.getPromotionAmount(),
                    supplements
            );
        }).collect(Collectors.toList());

        return new OrderDetailsDTO(
                order.getId(),
                items,
                order.getTotal(),
                order.getOrderStatus(),
                order.getDeliveryAddress(),
                order.getDeliveryInfo(),
                order.getRestaurant(),
                order.getPaymentMethod(),
                order.getRejectionReason(),
                order.getAppliedPlatformDiscount(),
                order.getAppliedPlatformPromotionId(),
                order.getAmountGivenByClient(),
                order.getChangeToReturn(),
                order.getAppliedClientPromoCode(),
                order.getClientPromoDiscount()
        );
    }
}
