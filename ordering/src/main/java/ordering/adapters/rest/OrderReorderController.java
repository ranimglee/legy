package ordering.adapters.rest;

import lombok.RequiredArgsConstructor;
import ordering.application.exception.OrderNotFoundException;
import ordering.domain.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import shared.config.security.JwtUtil;
import shared.events.ReorderCommand;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderReorderController {

    private static final String TOPIC = "reorder-commands";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final JwtUtil jwtUtil;
    private final OrderRepository orderRepository;

    @PostMapping("/{orderId}/reorder")
    public ResponseEntity<Void> reorder(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String orderId
    ) {

        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.extractUserIdFromAccessToken(token);

        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        var items = order.getItems().stream()
                .map(i -> new ReorderCommand.Item(i.getProductId(), i.getQuantity()))
                .collect(Collectors.toList());

        kafkaTemplate.send(TOPIC, new ReorderCommand(userId, items));
        return ResponseEntity.accepted().build();
    }
}
