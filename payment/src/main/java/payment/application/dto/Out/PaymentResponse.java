package payment.application.dto.Out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import payment.domain.model.Payment;
import payment.domain.model.PaymentCalendar;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    private String message;
    private List<Payment> payments;
    private String error;

    public PaymentResponse(String message, List<Payment> payments) {
        this.message = message;
        this.payments = payments;
    }

    public PaymentResponse(String error) {
        this.error = error;
    }


}