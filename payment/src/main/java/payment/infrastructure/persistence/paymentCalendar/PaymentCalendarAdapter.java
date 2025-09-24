package payment.infrastructure.persistence.paymentCalendar;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import payment.domain.model.PaymentCalendar;
import payment.domain.repository.PaymentCalendarRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PaymentCalendarAdapter implements PaymentCalendarRepository {
    private final PaymentCalendarMongoRepository paymentCalendarMongoRepository;

    @Autowired
    public PaymentCalendarAdapter(PaymentCalendarMongoRepository paymentCalendarMongoRepository) {
        this.paymentCalendarMongoRepository = paymentCalendarMongoRepository;
    }

    @Override
    public Optional<PaymentCalendar> findByDueDate(Date dueDate) {
        return paymentCalendarMongoRepository.findByDueDate(dueDate)
                .map(PaymentCalendarDocument::toDomain);
    }



    @Override
    public PaymentCalendar save(PaymentCalendar calendar) {
        PaymentCalendarDocument document = PaymentCalendarDocument.fromDomain(calendar);
        return paymentCalendarMongoRepository.save(document).toDomain();
    }
}
