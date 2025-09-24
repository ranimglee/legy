package restaurant.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import restaurant.domain.model.ProductFavorite;
import restaurant.domain.repository.ProductFavoriteRepository;
import restaurant.infrastructure.Document.MongoProductFavorite;
import restaurant.infrastructure.mapper.ProductFavoriteMapper;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductFavoriteRepositoryImpl implements ProductFavoriteRepository {

    private final SpringDataProductFavoriteRepository springRepo;

    @Override
    public ProductFavorite save(ProductFavorite fav) {
        MongoProductFavorite m = ProductFavoriteMapper.toMongo(fav);
        MongoProductFavorite saved = springRepo.save(m);
        return ProductFavoriteMapper.toDomain(saved);
    }

    @Override
    public Optional<ProductFavorite> findByUserIdAndProductId(String userId, String productId) {
        return springRepo.findByUserIdAndProductId(userId, productId)
                .map(ProductFavoriteMapper::toDomain);
    }

    @Override
    public void deleteByUserIdAndProductId(String userId, String productId) {
        springRepo.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    public Page<ProductFavorite> findByUserId(String userId, Pageable pageable) {
        return springRepo.findByUserId(userId, pageable)
                .map(ProductFavoriteMapper::toDomain);
    }
}
