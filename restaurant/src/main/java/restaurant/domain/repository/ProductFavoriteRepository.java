package restaurant.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import restaurant.domain.model.ProductFavorite;

import java.util.Optional;

public interface ProductFavoriteRepository {
    ProductFavorite save(ProductFavorite fav);

    Optional<ProductFavorite> findByUserIdAndProductId(String userId, String productId);

    void deleteByUserIdAndProductId(String userId, String productId);

    Page<ProductFavorite> findByUserId(String userId, Pageable pageable);
}
