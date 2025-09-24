package restaurant.infrastructure.mapper;

import restaurant.domain.model.ProductFavorite;
import restaurant.infrastructure.Document.MongoProductFavorite;

public class ProductFavoriteMapper {
    public static MongoProductFavorite toMongo(ProductFavorite d) {
        if (d == null) return null;
        var m = new MongoProductFavorite();
        m.setId(d.getId());
        m.setUserId(d.getUserId());
        m.setProductId(d.getProductId());
        return m;
    }

    public static ProductFavorite toDomain(MongoProductFavorite m) {
        if (m == null) return null;
        var d = new ProductFavorite();
        d.setId(m.getId());
        d.setUserId(m.getUserId());
        d.setProductId(m.getProductId());
        return d;
    }
}
