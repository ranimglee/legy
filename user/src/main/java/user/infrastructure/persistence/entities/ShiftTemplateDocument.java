package user.infrastructure.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import user.domain.model.ShiftType;

import java.time.LocalTime;

@Document(collection = "shift_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftTemplateDocument {
    @Id
    private String id;

    private ShiftType shiftType;

    private LocalTime startTime;

    private LocalTime endTime;

    private int maxCouriers;
}
