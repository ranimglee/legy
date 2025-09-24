package payment.adapters.rest;

import com.stripe.exception.StripeException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payment.application.dto.In.PaymentRequest;
import payment.application.dto.Out.PaymentCalendarResponse;
import payment.application.dto.Out.PaymentResponse;
import payment.application.usecase.*;
import payment.config.JwtConfiguration;
import payment.domain.exception.Invalid2FACodeException;
import payment.domain.model.Payment;
import payment.domain.model.PaymentCalendar;
import payment.domain.model.PaymentMethod;
import payment.domain.service.PaymentDomainService;
import shared.config.security.JwtUtil;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/financier/payments")
@AllArgsConstructor
public class PaymentController {

    private final AddPaymentUseCase addPaymentUseCase;
    private final GetPaymentsUseCase getPaymentsUseCase;
    private final CancelPaymentUseCase cancelPaymentUseCase;
    private final PaymentDomainService paymentDomainService;

    private final JwtUtil jwtUtil; // Inject JWT config

    @PostMapping("/add-payment")
    public ResponseEntity<PaymentCalendarResponse> addPayment(
            @Valid @RequestBody PaymentRequest request,
            HttpServletRequest httpRequest) {

        try {
            String authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);  // remove "Bearer " prefix
                String userId = jwtUtil.extractUserIdFromAccessToken(token);
                request.setUserId(userId);
                System.out.println("Extracted UserId from JWT: " + userId);
            } else {
                throw new RuntimeException("JWT token missing or invalid.");
            }

            List<PaymentCalendar> payments = addPaymentUseCase.execute(request);
            PaymentCalendarResponse response = new PaymentCalendarResponse("Payment added successfully", payments);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PaymentCalendarResponse("Error adding payment: " + e.getMessage()));
        }
    }


    @GetMapping("/filter")
    public ResponseEntity<PaymentResponse> getFilteredPayments(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {

        try {
            List<Payment> payments = getPaymentsUseCase.execute(date, type, status);
            if (payments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new PaymentResponse("No payments found for the specified filter"));
            }
            return ResponseEntity.ok(new PaymentResponse("Payments fetched successfully", payments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PaymentResponse("Error fetching payments: " + e.getMessage()));
        }
    }

    @GetMapping("/get-payment-by-id/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable("id") String paymentId) {
        try {
            Payment payment = paymentDomainService.getPaymentById(paymentId);
            return ResponseEntity.ok(new PaymentResponse("Payment fetched successfully", List.of(payment)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new PaymentResponse("Payment not found"));
        }
    }

    @GetMapping("/get-all-payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        try {
            List<Payment> payments = getPaymentsUseCase.getAllPayments();
            if (payments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }


    @PutMapping("/update-payment/{paymentId}")
    public ResponseEntity<PaymentResponse> modifyPayment(
            @PathVariable String paymentId,
            @RequestParam(required = false) Double amount,
            @RequestParam(required = false) Date newDate,
            @RequestParam(required = false) PaymentMethod method,
            @RequestParam(required = false) String beneficiary) {

        try {
            Payment updatedPayment = getPaymentsUseCase.modifyPayment(paymentId, amount, newDate, method, beneficiary);
            return ResponseEntity.ok(new PaymentResponse("Payment updated successfully", List.of(updatedPayment)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new PaymentResponse("Payment not found"));
        }
    }

    @PutMapping("/postpone-payment/{paymentId}")
    public ResponseEntity<PaymentResponse> postponePayment(
            @PathVariable String paymentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date newDate) {

        try {
            Payment updatedPayment = getPaymentsUseCase.postponePayment(paymentId, newDate);
            return ResponseEntity.ok(new PaymentResponse("Payment postponed successfully", List.of(updatedPayment)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new PaymentResponse("Payment not found"));
        }
    }

    @PatchMapping("/cancel/{paymentId}")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable String paymentId) {
        try {
            Payment updatedPayment = cancelPaymentUseCase.cancelPayment(paymentId);
            return ResponseEntity.ok(new PaymentResponse("Payment canceled successfully", List.of(updatedPayment)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new PaymentResponse("Payment not found"));
        }
    }



    @GetMapping("/processing-payments")
    public ResponseEntity<List<Payment>> getProcessingPayments() {
        List<Payment> processingPayments = getPaymentsUseCase.getProcessingPayments();
        return ResponseEntity.ok(processingPayments);
    }


}
