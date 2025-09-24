package payment.adapters.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payment.application.dto.In.PaymentMethodRequest;
import payment.application.dto.Out.PaymentCalendarResponse;
import payment.application.service.PaymentMethodService;
import payment.application.usecase.AddPaymentMethodUseCase;
import payment.config.JwtConfiguration;
import payment.domain.model.PaymentMethod;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/financier/payment-methods")
@AllArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;
    private final AddPaymentMethodUseCase addPaymentMethodUseCase;
    private final JwtConfiguration jwtConfiguration; // Inject JWT config



    /**
     * Creates a new payment method.
     *
     * @param paymentMethodRequest the request containing payment method details
     * @return the created payment method wrapped in a ResponseEntity with HTTP status 201
     */
    @PostMapping("/create-payment-method")
    public ResponseEntity<PaymentMethod> createPaymentMethod(@RequestBody @Valid PaymentMethodRequest paymentMethodRequest,
                                                             HttpServletRequest httpRequest) {

            // Extract UserId from JWT Token
            String authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);  // remove "Bearer " prefix
                String userId = jwtConfiguration.extractUserIdFromAccessToken(token);
                paymentMethodRequest.setUserId(userId);
                System.out.println("Extracted UserId from JWT: " + userId);
            } else {
                throw new RuntimeException("JWT token missing or invalid.");
            }

            // Log the incoming paymentMethodRequest to ensure it's being parsed correctly
            System.out.println("Received PaymentMethodRequest: " + paymentMethodRequest);

            // Create Payment Method
            PaymentMethod createdPaymentMethod = addPaymentMethodUseCase.execute(paymentMethodRequest);

            // Return the created payment method as part of the response
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPaymentMethod);


    }

    /**
     * Retrieves a payment method by its unique identifier.
     *
     * @param id the unique identifier of the payment method to retrieve

*/
    @GetMapping("/get-payment-method-by-id/{id}")
    public ResponseEntity<PaymentMethod> getPaymentMethodById(@PathVariable String id) {
        Optional<PaymentMethod> paymentMethod = paymentMethodService.getPaymentMethodById(id);
        return paymentMethod.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    /**
     * Updates a payment method identified by its unique identifier.
     *
     * @param id the unique identifier of the payment method to update
     * @param paymentMethodRequest the updated payment method details
     * @return the updated payment method wrapped in a ResponseEntity with HTTP status 200
     *         or 404 if not found
     */
    @PutMapping("/update-payment-method/{id}")
    public ResponseEntity<PaymentMethod> updatePaymentMethod(@PathVariable String id, @RequestBody @Valid PaymentMethodRequest paymentMethodRequest) {
        Optional<PaymentMethod> updated = paymentMethodService.updatePaymentMethod(id, paymentMethodRequest);
        return updated.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());  // Return 404 if not found
    }


    /**
     * Deletes a payment method by its unique identifier.
     *
     * @param id the unique identifier of the payment method to delete
     * @return a ResponseEntity with HTTP status 204 if the payment method was deleted
     *         or 404 if not found
     */
    @DeleteMapping("/delete-payment-method/{id}")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable String id) {
        boolean deleted = paymentMethodService.deletePaymentMethod(id);
        return deleted ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * Retrieves all payment methods.
     *
     * @return a ResponseEntity containing all payment methods and HTTP status 200
     */
    @GetMapping("/get-all-payment-methods")
    public ResponseEntity<List<PaymentMethod>> getAllPaymentMethods() {
        List<PaymentMethod> methods = paymentMethodService.getAllPaymentMethods();
        return ResponseEntity.ok(methods);
    }


}
