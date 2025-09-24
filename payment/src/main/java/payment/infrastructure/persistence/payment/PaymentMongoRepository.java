package payment.infrastructure.persistence.payment;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import payment.domain.model.enums.PaymentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@Repository

public interface PaymentMongoRepository extends MongoRepository<PaymentDocument, String> {
    List<PaymentDocument> findByDate(Date date);
    List<PaymentDocument> findByStatus(PaymentStatus status);
    List<PaymentDocument> findByDateAndStatus(Date Date,PaymentStatus status);

}