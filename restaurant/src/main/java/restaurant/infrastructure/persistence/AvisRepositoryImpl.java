package restaurant.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import restaurant.domain.model.Avis;
import restaurant.domain.repository.AvisRepository;
import restaurant.infrastructure.mapper.AvisMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AvisRepositoryImpl implements AvisRepository {
    private final MongoAvisRepository mongoRepo;

    @Override
    public Avis save(Avis avis) {
        return AvisMapper.toDomain(mongoRepo.save(AvisMapper.toMongo(avis)));
    }

    @Override
    public List<Avis> findByRestaurantId(String restaurantId) {
        return mongoRepo.findAllByRestaurantId(restaurantId)
                .stream()
                .map(AvisMapper::toDomain)
                .collect(Collectors.toList());
    }


    @Override
    public Optional<Avis> findByRestaurantIdAndUserId(String restaurantId, String userId) {
        return mongoRepo
                .findByRestaurantIdAndUserId(restaurantId, userId)
                .map(AvisMapper::toDomain);
    }

    @Override
    public Page<Avis> findByRestaurantId(String restaurantId, Pageable pageable) {
        return mongoRepo.findByRestaurantId(restaurantId, pageable)
                .map(AvisMapper::toDomain);
    }


}
