package user.infrastructure.mapper;

import user.domain.model.Shift;
import user.infrastructure.persistence.entities.ShiftDocument;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ShiftMapper {
    public static ShiftDocument toDocument(Shift domain) {
        ShiftDocument doc = new ShiftDocument();
        doc.setShiftId(domain.getShiftId());

        // Convert LocalDate → Date
        doc.setDate(Date.from(domain.getDate()
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
        ));
        doc.setStartTime(domain.getStartTime());
        doc.setEndTime(domain.getEndTime());
        doc.setShiftType(domain.getShiftType());
        doc.setMaxCouriers(domain.getMaxCouriers());
        doc.setAssignedCount(domain.getAssignedCount());
        doc.setDriverIds(domain.getDriverIds());


        doc.setZoneId(domain.getZoneId());
        return doc;
    }

    public static Shift toDomain(ShiftDocument doc) {
        Shift domain = new Shift();
        domain.setShiftId(doc.getShiftId());
        // Convert Date → Instant → LocalDate
        domain.setDate(doc.getDate()
                .toInstant()
                .atZone(ZoneOffset.UTC)   // or ZoneId.systemDefault()
                .toLocalDate()
        );
        domain.setStartTime(doc.getStartTime());
        domain.setEndTime(doc.getEndTime());
        domain.setShiftType(doc.getShiftType());
        domain.setMaxCouriers(doc.getMaxCouriers());
        domain.setAssignedCount(doc.getAssignedCount());
        domain.setDriverIds(doc.getDriverIds());
        domain.setZoneId(doc.getZoneId());
        return domain;
    }
}
