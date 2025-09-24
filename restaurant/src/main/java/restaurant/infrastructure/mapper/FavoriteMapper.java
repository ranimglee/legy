package restaurant.infrastructure.mapper;

import restaurant.domain.model.Favorite;
import restaurant.infrastructure.Document.MongoFavorite;

public class FavoriteMapper {
    public static MongoFavorite toMongo(Favorite domain) {
        if (domain == null) return null;
        MongoFavorite m = new MongoFavorite();
        m.setId(domain.getId());
        m.setUserId(domain.getUserId());
        m.setRestaurantId(domain.getRestaurantId());
        return m;
    }

    public static Favorite toDomain(MongoFavorite m) {
        if (m == null) return null;
        Favorite f = new Favorite();
        f.setId(m.getId());
        f.setUserId(m.getUserId());
        f.setRestaurantId(m.getRestaurantId());
        return f;
    }
}
