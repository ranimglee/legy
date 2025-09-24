package user.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import user.domain.model.DeliveryZone;
import user.infrastructure.persistence.entities.ZoneDocument;

import java.util.Optional;

@Repository
public interface MongoZoneRepository extends MongoRepository<ZoneDocument, String> {
    @Query("{ 'assignedRestaurants.id' : ?0 }")
    Optional<ZoneDocument> findByAssignedRestaurantId(String restaurantId);

}
