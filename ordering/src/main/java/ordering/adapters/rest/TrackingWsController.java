package ordering.adapters.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ordering.application.dto.order.LocationUpdateDTO;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TrackingWsController {

    private final SimpMessagingTemplate template;

    @MessageMapping("/livreur/{livreurId}/location")
    public void handleLocation(@DestinationVariable String livreurId,
                               @Payload LocationUpdateDTO loc) {

        template.convertAndSend("/topic/orders/" + loc.getOrderId() + "/location", loc);
        log.debug("📡 relayed position for order {} ({}): {} {}",
                loc.getOrderId(), livreurId, loc.getLatitude(), loc.getLongitude());
    }
}
