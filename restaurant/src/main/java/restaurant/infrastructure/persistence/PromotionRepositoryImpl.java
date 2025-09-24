package restaurant.infrastructure.persistence;

import org.springframework.stereotype.Repository;
import restaurant.domain.model.Promotion;
import restaurant.domain.repository.PromotionRepository;
import restaurant.infrastructure.Document.MongoPromotion;
import restaurant.infrastructure.mapper.PromotionMapper;
import org.springframework.data.mongodb.repository.MongoRepository;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public  class PromotionRepositoryImpl implements PromotionRepository {
    private final SpringDataPromotionRepository repo;


    public Promotion save(Promotion promotion) {
        MongoPromotion mongo = repo.save(PromotionMapper.toMongo(promotion));
        return PromotionMapper.toDomain(mongo);
    }

    public Optional<Promotion> findById(String id) {
        return repo.findById(id).map(PromotionMapper::toDomain);
    }




    @Override
    public List<Promotion> findAll() {
        return repo.findAll().stream()
                .map(PromotionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Promotion> findByRestaurantId(String restaurantId) {
        return repo.findByRestaurantId(restaurantId).stream()
                .map(PromotionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Promotion promotion) {
        repo.delete(PromotionMapper.toMongo(promotion));
    }


}

interface SpringDataPromotionRepository extends MongoRepository<MongoPromotion, String> {
    List<MongoPromotion> findByRestaurantId(String restaurantId);

}


