package payment.infrastructure.persistence.payment;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import payment.domain.model.Payment;
import payment.domain.model.enums.PaymentStatus;
import payment.domain.repository.PaymentRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PaymentAdapter implements PaymentRepository {
    private final PaymentMongoRepository paymentMongoRepository;

    @Autowired
    public PaymentAdapter(PaymentMongoRepository paymentMongoRepository) {
        this.paymentMongoRepository = paymentMongoRepository;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentDocument document = PaymentDocument.fromDomain(payment);
        return paymentMongoRepository.save(document).toDomain();
    }

    @Override
    public Optional<Payment> findById(String id) {
        return paymentMongoRepository.findById(id).map(PaymentDocument::toDomain);
    }

    @Override
    public List<Payment> findAll() {
        return paymentMongoRepository.findAll().stream()
                .map(PaymentDocument::toDomain)
                .collect(Collectors.toList());
    }



    @Override
    public List<Payment> findByStatus(PaymentStatus status) {
        return paymentMongoRepository.findByStatus(status).stream()
                .map(PaymentDocument::toDomain)
                .collect(Collectors.toList());
    }


    @Override
    public List<Payment> findByDate(Date date) {
        return paymentMongoRepository.findByDate(date).stream()
                .map(PaymentDocument::toDomain)
                .collect(Collectors.toList());
    }





}
