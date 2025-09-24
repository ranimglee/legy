package user.infrastructure.mapper;

import user.domain.model.ShiftTemplate;
import user.infrastructure.persistence.entities.ShiftTemplateDocument;

public class ShiftTemplateMapper {
    public static ShiftTemplate toDomain(ShiftTemplateDocument document) {
        if (document == null) return null;
        return ShiftTemplate.builder()
                .id(document.getId())
                .shiftType(document.getShiftType())
                .startTime(document.getStartTime())
                .endTime(document.getEndTime())
                .maxCouriers(document.getMaxCouriers())
                .build();
    }

    public static ShiftTemplateDocument toDocument(ShiftTemplate domain) {
        if (domain == null) return null;
        return ShiftTemplateDocument.builder()
                .id(domain.getId())
                .shiftType(domain.getShiftType())
                .startTime(domain.getStartTime())
                .endTime(domain.getEndTime())
                .maxCouriers(domain.getMaxCouriers())
                .build();
    }
}
