package restaurant.adapters.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import restaurant.application.dto.EvaluateProduct.AverageProductRatingResponse;
import restaurant.application.dto.EvaluateProduct.EvaluateProductRequest;
import restaurant.application.dto.EvaluateProduct.EvaluateProductResponse;
import restaurant.application.service.ProductEvaluationService;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/evaluations")
@RequiredArgsConstructor
public class ProductEvaluationController {

    private final ProductEvaluationService service;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EvaluateProductResponse evaluate(
            @PathVariable String productId,
            @AuthenticationPrincipal(expression = "id") String clientId,
            @Valid @RequestBody EvaluateProductRequest request
    ) {
        return service.evaluate(productId, clientId, request);
    }

    @GetMapping
    public List<EvaluateProductResponse> list(
            @PathVariable String productId
    ) {
        return service.listForProduct(productId);
    }

    @GetMapping("/average")
    public AverageProductRatingResponse average(
            @PathVariable String productId
    ) {
        return service.averageForProduct(productId);
    }


}
