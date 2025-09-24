package payment.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import payment.domain.model.PaymentCalendar;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PaymentCalendarRepository {
    Optional<PaymentCalendar> findByDueDate(Date dueDate);
    PaymentCalendar save(PaymentCalendar calendar);
}

