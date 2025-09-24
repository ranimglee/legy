package ordering.domain.repository;

import ordering.domain.model.Payout;

import java.util.List;
import java.util.Optional;

public interface PayoutRepository {
    void save(Payout payout);
    List<Payout> findAllByRestaurantIdOrderByPayoutDateDesc(String restaurantId);

    List<Payout> findAll();

    Optional<Payout> findById(String id);
    Optional<Payout> findFirstByRestaurantIdOrderByPayoutDateDesc(String restaurantId);
}
