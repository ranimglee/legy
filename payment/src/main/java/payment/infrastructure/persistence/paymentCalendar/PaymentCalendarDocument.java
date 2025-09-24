package payment.infrastructure.persistence.paymentCalendar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import payment.domain.model.Payment;
import payment.domain.model.PaymentCalendar;
import payment.infrastructure.persistence.payment.PaymentDocument;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "payment_calendars")
public class PaymentCalendarDocument {
    @Id
    private String id;
    private Date dueDate;
    private List<PaymentDocument> payments;

    public static PaymentCalendarDocument fromDomain(PaymentCalendar calendar) {
        return new PaymentCalendarDocument(
                calendar.getId(),
                calendar.getDueDate(),
                calendar.getPayments().stream()
                        .map(PaymentDocument::fromDomain)
                        .collect(Collectors.toList())
        );
    }

    public PaymentCalendar toDomain() {
        return new PaymentCalendar(
                id,
                dueDate,
                payments.stream()
                        .map(PaymentDocument::toDomain)
                        .collect(Collectors.toList())
        );
    }

}
