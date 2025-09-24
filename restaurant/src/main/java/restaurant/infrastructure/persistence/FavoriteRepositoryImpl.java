package restaurant.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import restaurant.domain.model.Favorite;
import restaurant.domain.repository.FavoriteRepository;
import restaurant.infrastructure.Document.MongoFavorite;
import restaurant.infrastructure.mapper.FavoriteMapper;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FavoriteRepositoryImpl implements FavoriteRepository {

    private final SpringDataFavoriteRepository springRepo;

    @Override
    public Favorite save(Favorite fav) {
        MongoFavorite m = FavoriteMapper.toMongo(fav);
        MongoFavorite saved = springRepo.save(m);
        return FavoriteMapper.toDomain(saved);
    }

    @Override
    public Optional<Favorite> findByUserIdAndRestaurantId(
            String userId, String restaurantId
    ) {
        return springRepo
                .findByUserIdAndRestaurantId(userId, restaurantId)
                .map(FavoriteMapper::toDomain);
    }

    @Override
    public Page<Favorite> findByUserId(String userId, Pageable pageable) {
        return springRepo.findByUserId(userId, pageable)
                .map(FavoriteMapper::toDomain);
    }

    @Override
    public void deleteByUserIdAndRestaurantId(
            String userId, String restaurantId
    ) {
        springRepo.deleteByUserIdAndRestaurantId(userId, restaurantId);
    }
}
