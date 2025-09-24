package user.infrastructure.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import user.domain.model.LivreurEntity;
import user.domain.model.ShiftStatus;
import user.domain.model.ShiftType;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Document
@NoArgsConstructor
public class ShiftDocument {
    @Id
    private String shiftId;
    private int maxCouriers;
    private int assignedCount;
    private ShiftType shiftType;
    private Date date;
    private LocalTime startTime;           // e.g. 11:30
    private LocalTime endTime;
    private List<String> driverIds; // Use IDs instead of DBRefs
    private String zoneId; // Needed for per-zone filtering


}
