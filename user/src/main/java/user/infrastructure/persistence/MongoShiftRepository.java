package user.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import user.domain.model.ShiftType;
import user.infrastructure.persistence.entities.ShiftDocument;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface MongoShiftRepository extends MongoRepository<ShiftDocument, String> {

    boolean existsByZoneIdAndDateAndShiftType(String zoneId, LocalDate date, ShiftType type);

    @Query("{ 'zoneId': ?0, 'date': { $gte: ?1, $lte: ?2 } }")
    List<ShiftDocument> findAllByZoneIdAndDateRange(String zoneId, LocalDate startDate, LocalDate endDate);


    Collection<ShiftDocument> findByZoneIdAndDate(String zoneId, LocalDate date);

}
