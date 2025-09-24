package ordering.infrastructure.mapper;


import ordering.domain.dto.RefusalResponseDTO;
import ordering.domain.model.RefusalHistory;
import ordering.infrastructure.Document.MongoRefusalHistory;

public class RefusalMapper {
    public static MongoRefusalHistory toMongo(RefusalHistory domain) {
        return MongoRefusalHistory.builder()
                .id(domain.getId())
                .orderId(domain.getOrderId())
                .livreurId(domain.getLivreurId())
                .reason(domain.getReason())
                .refusedAt(domain.getRefusedAt())
                .build();
    }

    public static RefusalHistory toDomain(MongoRefusalHistory mongo) {
        return RefusalHistory.builder()
                .id(mongo.getId())
                .orderId(mongo.getOrderId())
                .livreurId(mongo.getLivreurId())
                .reason(mongo.getReason())
                .refusedAt(mongo.getRefusedAt())
                .build();
    }

    public static RefusalResponseDTO toDTO(RefusalHistory domain) {
        return new RefusalResponseDTO(
                domain.getId(),
                domain.getOrderId(),
                domain.getLivreurId(),
                domain.getReason(),
                domain.getRefusedAt()
        );
    }
}