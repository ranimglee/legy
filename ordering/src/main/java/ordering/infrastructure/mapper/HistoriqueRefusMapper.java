package ordering.infrastructure.mapper;

import ordering.domain.model.HistoriqueRefus;
import ordering.infrastructure.Document.MongoHistoriqueRefus;

public class HistoriqueRefusMapper {

    public static MongoHistoriqueRefus toMongo(HistoriqueRefus domain) {
        if (domain == null) return null;

        return new MongoHistoriqueRefus(
                domain.getOrderId(),
                domain.getStatus(),
                domain.getTimestamp(),
                domain.getMessage()
        );
    }

    public static HistoriqueRefus toDomain(MongoHistoriqueRefus mongo) {
        if (mongo == null) return null;

        return new HistoriqueRefus(
                mongo.getOrderId(),
                mongo.getStatus(),
                mongo.getTimestamp(),
                mongo.getMessage()
        );
    }
}
