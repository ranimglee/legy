package payment.infrastructure.persistence.paymentMethod;

import org.springframework.stereotype.Repository;
import payment.application.dto.In.PaymentMethodRequest;
import payment.domain.model.PaymentMethod;
import payment.domain.repository.PaymentMethodRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository

public class PaymentMethodAdapter implements PaymentMethodRepository {
    private final PaymentMethodMongoRepository paymentMethodMongoRepository;

    public PaymentMethodAdapter(PaymentMethodMongoRepository paymentMethodMongoRepository) {
        this.paymentMethodMongoRepository = paymentMethodMongoRepository;
    }
    @Override
    public Optional<PaymentMethod> findById(String id) {
        return paymentMethodMongoRepository.findById(id)
                .map(PaymentMethodDocument::toDomain);
    }



    @Override
    public boolean existsById(String id) {
        return paymentMethodMongoRepository.existsById(id);
    }




    @Override
    public PaymentMethod save(PaymentMethod paymentMethod) {
        PaymentMethodDocument document = PaymentMethodDocument.fromDomain(paymentMethod);

        PaymentMethodDocument saved = paymentMethodMongoRepository.save(document);

        return saved.toDomain();
    }

    @Override
    public void deleteById(String id) {
        paymentMethodMongoRepository.deleteById(id);
    }

    @Override
    public List<PaymentMethod> findAll() {
        return paymentMethodMongoRepository.findAll().stream()
                .map(PaymentMethodDocument::toDomain).toList();
    }

    @Override
    public List<PaymentMethod> findByUserId(String userId) {
        return paymentMethodMongoRepository.findByUserId(userId).stream()
                .map(PaymentMethodDocument::toDomain).toList() ;
    }
}
