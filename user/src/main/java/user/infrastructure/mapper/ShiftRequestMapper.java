package user.infrastructure.mapper;

import user.domain.model.ShiftRequest;
import user.infrastructure.persistence.entities.ShiftRequestDocument;

public class ShiftRequestMapper {

    public static ShiftRequestDocument toDocument(ShiftRequest request) {
        ShiftRequestDocument doc = new ShiftRequestDocument();
        doc.setId(request.getId());
        doc.setLivreurId(request.getLivreurId());
        doc.setZoneId(request.getZoneId());
        // Map domain Shift → persistence ShiftDocument
        doc.setShift(ShiftMapper.toDocument(request.getShift()));
        doc.setStatus(request.getStatus());
        return doc;
    }

    public static ShiftRequest toDomain(ShiftRequestDocument doc) {
        ShiftRequest domain = new ShiftRequest();
        domain.setId(doc.getId());
        domain.setLivreurId(doc.getLivreurId());
        domain.setZoneId(doc.getZoneId());
        domain.setStatus(doc.getStatus());
        domain.setShift(ShiftMapper.toDomain(doc.getShift()));
        return domain;
    }
}
