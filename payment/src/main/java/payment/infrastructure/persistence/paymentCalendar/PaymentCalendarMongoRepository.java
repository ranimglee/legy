package payment.infrastructure.persistence.paymentCalendar;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@Repository

public interface PaymentCalendarMongoRepository extends MongoRepository<PaymentCalendarDocument, String> {
    Optional<PaymentCalendarDocument> findByDueDate(Date dueDate);
}