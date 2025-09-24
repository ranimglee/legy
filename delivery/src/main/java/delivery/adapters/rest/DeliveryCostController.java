package delivery.adapters.rest;


import delivery.application.dto.in.DeliveryCostRequest;
import delivery.application.usecase.CalculateDeliveryCostUseCase;
import delivery.domain.model.DeliveryCost;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/financier/delivery")
@AllArgsConstructor
public class DeliveryCostController {
    private final CalculateDeliveryCostUseCase useCase;


    @PostMapping("/calculate")
    public ResponseEntity<DeliveryCost> calculate(@RequestBody DeliveryCostRequest request) {
        DeliveryCost cost = useCase.execute(request.getDistanceKm(), request.getCostPerKm(), request.getOrderId());
        return ResponseEntity.ok(cost);
    }


}
