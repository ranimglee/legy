package user.infrastructure.kafka;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shared.events.ReorderCommand;
import user.application.dto.in.AddToCartRequest;
import user.application.service.CartService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReorderListener {

    private final CartService cartService;

    @KafkaListener(
            topics = "reorder-commands",
            groupId = "user-cart-service",
            containerFactory = "reorderKafkaListenerContainerFactory"
    )
    public void onReorderCommand(ReorderCommand cmd) {
        log.info("▶ Received ReorderCommand for user={} with {} items",
                cmd.userId(), cmd.items().size());

        cartService.clearClientCart(cmd.userId());

        for (ReorderCommand.Item item : cmd.items()) {
            log.info("   • Adding to cart: {} x{}", item.productId(), item.quantity());
            cartService.addToClientCart(
                    cmd.userId(),
                    new AddToCartRequest(item.productId(), item.quantity())
            );
        }

        log.info("✅ Cart refill complete for user {}", cmd.userId());
    }
}
