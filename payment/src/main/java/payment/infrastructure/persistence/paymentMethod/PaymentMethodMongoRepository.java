package payment.infrastructure.persistence.paymentMethod;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import payment.domain.model.PaymentMethod;

import java.util.Collection;
import java.util.List;

@Repository
public interface PaymentMethodMongoRepository extends MongoRepository<PaymentMethodDocument, String> {

    List<PaymentMethodDocument> findByUserId(String userId);
}
