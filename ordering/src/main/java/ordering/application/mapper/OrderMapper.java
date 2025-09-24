package ordering.application.mapper;

import ordering.application.dto.order.ClientInfoDTO;
import ordering.application.dto.order.OrderItemDTO;
import ordering.application.dto.order.OrderRequestDTO;
import ordering.application.dto.order.SupplementSelection;
import ordering.domain.model.Order;
import ordering.domain.model.OrderItem;
import ordering.domain.model.value.ClientInfo;
import ordering.domain.model.value.RestaurantInfo;
import org.springframework.stereotype.Component;
import shared.domain.service.RestaurantQueryService;
import shared.dto.ClientProfileDTO;
import shared.dto.RestaurantInfoDTO;
import shared.port.ProductQueryPort;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private final RestaurantQueryService restaurantQueryService;
    private final ProductQueryPort productQueryPort;
    public OrderMapper(RestaurantQueryService restaurantQueryService, ProductQueryPort productQueryPort) {
        this.restaurantQueryService = restaurantQueryService;
        this.productQueryPort = productQueryPort;
    }

    public Order toOrder(OrderRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Order.builder()
                .client(toClient(dto.client()))
                .restaurant(toRestaurant(dto.restaurantId()))
                .items(toOrderItemList(dto.items()))
                .build();
    }

    public ClientInfo toClient(ClientInfoDTO dto) {
        if (dto == null) {
            return null;
        }

        return new ClientInfo(
                dto.clientId(),
                dto.firstName(),
                dto.lastName(),
                dto.phone(),
                dto.address(),
                dto.longitude(),
                dto.latitude()
        );
    }





    // Manual mapping from List<OrderItemDTO> to List<OrderItem>
    public List<OrderItem> toOrderItemList(List<OrderItemDTO> dto) {
        if (dto == null) {
            return null;
        }

        return dto.stream()
                .map(this::toItem)  // Using direct field access in toItem
                .collect(Collectors.toList());
    }

    // Manual mapping from ClientProfileDTO to ClientInfo (direct conversion without needing ClientInfoDTO)
    public ClientInfo toClient(ClientProfileDTO dto) {
        if (dto == null) {
            return null;
        }

        return new ClientInfo(
                dto.id(),
                dto.firstname(),
                dto.lastname(),
                dto.phoneNumber(),
                dto.address(),
                dto.longitude(),
                dto.latitude()
        );
    }

    public RestaurantInfo toRestaurant(String restaurantId) {
        if (restaurantId == null) {
            return null;
        }

        RestaurantInfoDTO dto = restaurantQueryService.getRestaurantInfoById(restaurantId);

        return new RestaurantInfo(
                dto.restaurantId(),
                dto.name(),
                dto.phone(),
                dto.address(),
                dto.commission(),
                dto.longitude(),
                dto.latitude(),
                dto.totalRevenueCommission(),
                dto.nbrCommandesTotal(),
                dto.logo()

        );
    }

    public OrderItem toItem(OrderItemDTO dto) {
        if (dto == null) {
            return null;
        }

        List<SupplementSelection> supplements = null;
        if (dto.selectedSupplements() != null) {
            supplements = dto.selectedSupplements().stream()
                    .map(s -> new SupplementSelection(
                            s.supplementId(),
                            s.supplementName(),
                            s.quantity()
                    ))
                    .collect(Collectors.toList());
        }

        return new OrderItem(
                dto.productId(),
                dto.productName(),
                dto.productImage(),
                dto.unitPrice(),
                dto.quantity(),
                dto.promotionAmount(),
                supplements
        );
    }


}
