package payment.application.dto.In;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import payment.domain.model.enums.PaymentMethodType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class PaymentMethodRequest {

    @NotNull(message = "Payment method type is required.")
    private PaymentMethodType type;

    @NotNull(message = "Provider is required.")
    @Size(min = 3, max = 100, message = "Provider name must be between 3 and 100 characters.")
    private String provider;

    @Size(min = 10, max = 20, message = "Account number must be between 10 and 20 characters.")
    private String accountNumber;

    @NotNull(message = "Holder name is required.")
    @Size(min = 3, max = 100, message = "Holder name must be between 3 and 100 characters.")
    private String holderName;

    private boolean isDefault;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private String userId;

    // Optional fields for credit card or PayPal
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;
    // For credit card
    private String cvc;  // For credit card
    private String paypalEmail;  // For PayPal



    // Custom validation logic based on PaymentMethodType
    public void validatePaymentMethod() {
        if (type == PaymentMethodType.CREDIT_CARD) {
            if (expirationDate == null) {
                throw new IllegalArgumentException("Expiration date is required for CREDIT_CARD.");
            }
            if (cvc == null || cvc.isEmpty()) {
                throw new IllegalArgumentException("CVC is required for CREDIT_CARD.");
            }
        } else if (type == PaymentMethodType.PAYPAL) {
            if (paypalEmail == null || paypalEmail.isEmpty()) {
                throw new IllegalArgumentException("PayPal email is required for PAYPAL.");
            }
        } else if (type == PaymentMethodType.BANK_TRANSFER) {
            if (accountNumber == null || accountNumber.isEmpty()) {
                throw new IllegalArgumentException("Bank Routing Number is required for BANK_TRANSFER.");
            }
        }
    }
}
