package user.domain.repository;

import user.domain.model.DriverZoneAssignment;

import java.util.List;
import java.util.Optional;

public interface DriverZoneAssignmentRepository {

    DriverZoneAssignment save(DriverZoneAssignment assignment);


    long countByZoneIdAndAssignedTrue(String zoneId);


    Optional<DriverZoneAssignment> findById(String id);

    Optional<DriverZoneAssignment> findByLivreurIdAndZoneId(String livreurId, String zoneId);

    List<DriverZoneAssignment> findAllByLivreurId(String livreurId);

    List<DriverZoneAssignment> findByZoneIdAndAssignedTrue(String zoneId);

    List<DriverZoneAssignment> findByLivreurId(String livreurId);

}
