package restaurant.infrastructure.persistence;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import restaurant.domain.model.PromotionPlatform;
import restaurant.domain.repository.PromotionPlatformRepository;
import restaurant.infrastructure.Document.MongoPromotionPlatform;
import restaurant.infrastructure.mapper.PromotionPlatformMapper;
import shared.enums.PromotionType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Repository
public class PromotionPlatformRepositoryImpl implements PromotionPlatformRepository {
    private final MongoPromotionPlatformRepository repository;

    @Override
    public PromotionPlatform save(PromotionPlatform promotion) {
        MongoPromotionPlatform entity = PromotionPlatformMapper.toEntity(promotion);
        MongoPromotionPlatform saved = repository.save(entity);
        return PromotionPlatformMapper.toDomain(saved);
    }

    @Override
    public Optional<PromotionPlatform> findById(String promotionId) {
        return repository.findById(promotionId)
                .map(PromotionPlatformMapper::toDomain);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }



    @Override
    public List<PromotionPlatform> findAll() {
        return repository.findAll().stream()
                .map(PromotionPlatformMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<PromotionPlatform> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(PromotionPlatformMapper::toDomain);
    }

    @Override
    public Page<PromotionPlatform> findAllByType(PromotionType type, Pageable pageable) {
        return repository.findByType(type, pageable).map(PromotionPlatformMapper::toDomain);
    }



}
