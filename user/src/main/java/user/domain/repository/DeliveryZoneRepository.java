package user.domain.repository;

import user.domain.model.DeliveryZone;
import user.domain.model.LivreurEntity;

import java.util.List;
import java.util.Optional;

public interface DeliveryZoneRepository {
    DeliveryZone save(DeliveryZone zone);

    boolean existsById(String zoneId);

    void deleteById(String zoneId);

    Optional<DeliveryZone> findById(String zoneId);

    List<DeliveryZone> findAll();

    Optional<DeliveryZone> findByAssignedRestaurantId(String restaurantId);
}
