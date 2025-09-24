package payment.application.dto.Out;

import ch.qos.logback.core.joran.action.Action;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import payment.domain.model.PaymentCalendar;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCalendarResponse {
    private String message;
    private List<PaymentCalendar> payments;
    private String error;

    public PaymentCalendarResponse(String message, List<PaymentCalendar> payments) {
        this.message = message;
        this.payments = payments;
    }

    public PaymentCalendarResponse(String error) {
        this.error = error;
    }
}
