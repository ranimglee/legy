package ordering.application.usecase.order.updateOrderFlow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ordering.application.dto.order.SupplementSelection;
import ordering.application.dto.order.UpdateOrderRequestDTO;
import ordering.domain.model.Order;
import ordering.domain.model.OrderItem;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderItemUpdater {

    public void updateItems(Order order, UpdateOrderRequestDTO dto) {
        if (dto.items() != null && !dto.items().isEmpty()) {
            log.info("Updating items from DTO: {}", dto.items());
            order.setItems(dto.items().stream()
                    .map(i -> new OrderItem(
                            i.productId(),
                            i.productName(),
                            i.productImage(),
                            i.unitPrice(),
                            i.quantity(),
                            i.promotionAmount(),
                            i.selectedSupplements() != null ?
                                    i.selectedSupplements().stream()
                                            .map(s -> new SupplementSelection(
                                                    s.supplementId(),
                                                    s.supplementName(),
                                                    s.quantity()
                                            ))
                                            .toList()
                                    : null
                    )).toList());
        } else {
            log.info("DTO items are null or empty, retaining existing items: {}", order.getItems());
        }
    }
}