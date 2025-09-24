package user.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import user.domain.model.DriverZoneAssignment;
import user.domain.model.UserEntity;
import user.infrastructure.persistence.entities.MongoDriverZoneAssignment;

import java.util.List;
import java.util.Optional;

public interface MongoDriverZoneAssignmentRepository extends MongoRepository<MongoDriverZoneAssignment, String> {
    List<MongoDriverZoneAssignment> findByLivreurId(String livreurId);

    Optional<MongoDriverZoneAssignment> findByLivreurIdAndZoneId(String livreur, String zoneId);
    long countByZoneIdAndAssignedIsTrue(String zoneId);

    List<MongoDriverZoneAssignment> findAllByLivreurId(String livreurId);

    List<MongoDriverZoneAssignment> findByZoneIdAndAssignedTrue(String zoneId);
}