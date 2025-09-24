package delivery.adapters.rest;

import delivery.domain.service.GetDeliveriesByPersonIdUseCase;
import delivery.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery/")
@RequiredArgsConstructor
public class DeliveryController {

    private final GetDeliveriesByPersonIdUseCase getDeliveriesByPersonIdUseCase;

    @GetMapping("{personId}")
    public List<Order> getDeliveriesByPersonId(@PathVariable String personId) {
        return getDeliveriesByPersonIdUseCase.handle(personId);
    }
}
