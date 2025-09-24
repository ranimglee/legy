package user.domain.repository;

import user.domain.model.Shift;
import user.domain.model.ShiftStatus;
import user.domain.model.ShiftType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ShiftRepository {
     Optional<Shift> findById(String shiftId);


     Shift save(Shift shift);


    boolean existsByZoneIdAndDateAndShiftType(String zoneId, LocalDate date, ShiftType type);


    List<Shift> findShiftsByZoneAndDateBetween(String zoneId, LocalDate weekStart, LocalDate weekEnd);

    Set<ShiftType> findShiftTypesByZoneAndDate(String zoneId, LocalDate date);


    void deleteAllById(List<String> collect);
}
