package ordering.infrastructure.repository;

import io.micrometer.common.KeyValues;
import ordering.infrastructure.Document.PayoutDocument;

import org.apache.commons.lang3.concurrent.UncheckedFuture;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


public interface MongoPayoutRestoRepository extends MongoRepository<PayoutDocument,String> {

    List<PayoutDocument> findAllByRestaurantIdOrderByPayoutDateDesc(String restaurantId);

    Optional<PayoutDocument> findFirstByRestaurantIdOrderByPayoutDateDesc(String restaurantId);
}
