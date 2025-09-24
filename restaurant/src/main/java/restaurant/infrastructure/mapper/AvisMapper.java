package restaurant.infrastructure.mapper;

import restaurant.domain.model.Avis;
import restaurant.infrastructure.Document.MongoAvis;

public class AvisMapper {
    public static MongoAvis toMongo(Avis d) {
        if (d == null) return null;
        MongoAvis m = new MongoAvis();
        m.setId(d.getId());
        m.setRestaurantId(d.getRestaurantId());
        m.setUserId(d.getUserId());
        m.setScore(d.getScore());
        m.setComment(d.getComment());
        m.setCreatedAt(d.getCreatedAt());
        return m;
    }

    public static Avis toDomain(MongoAvis m) {
        if (m == null) return null;
        Avis d = new Avis();
        d.setId(m.getId());
        d.setRestaurantId(m.getRestaurantId());
        d.setUserId(m.getUserId());
        d.setScore(m.getScore());
        d.setComment(m.getComment());
        d.setCreatedAt(m.getCreatedAt());
        return d;
    }
}
