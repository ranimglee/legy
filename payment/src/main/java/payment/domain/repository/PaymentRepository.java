package payment.domain.repository;

import payment.domain.model.Payment;
import payment.domain.model.enums.PaymentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(String id);
    List<Payment> findAll();

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByDate(Date date);

}
