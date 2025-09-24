package payment.domain.repository;


import payment.application.dto.In.PaymentMethodRequest;
import payment.domain.model.PaymentMethod;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodRepository {

    Optional<PaymentMethod> findById(String id);


    boolean existsById(String id);


    PaymentMethod save(PaymentMethod paymentMethod);

    void deleteById(String id);

    List<PaymentMethod> findAll();

    List<PaymentMethod> findByUserId(String userId);
}

