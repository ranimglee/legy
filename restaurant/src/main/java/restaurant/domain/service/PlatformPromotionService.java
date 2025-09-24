package restaurant.domain.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import restaurant.domain.model.PromotionPlatform;
import restaurant.domain.repository.PromotionPlatformRepository;
import shared.enums.PromotionType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PlatformPromotionService {
    private final PromotionPlatformRepository repository;

    public PromotionPlatform addPromotion(PromotionPlatform promotion) {
        if (promotion.isActive()) {
            // Deactivate all other active promotions
            List<PromotionPlatform> existingPromos = repository.findAll();
            for (PromotionPlatform existing : existingPromos) {
                if (!existing.getId().equals(promotion.getId()) && existing.isActive()) {
                    existing.setActive(false);
                    repository.save(existing);
                }
            }
        }

        return repository.save(promotion);
    }

    public Optional<PromotionPlatform> toggleActiveStatus(String promotionId) {
        Optional<PromotionPlatform> existing = repository.findById(promotionId);
        if (existing.isPresent()) {
            PromotionPlatform promo = existing.get();
            boolean newStatus = !promo.isActive();
            promo.setActive(newStatus);

            // If activating this one, deactivate all others
            if (newStatus) {
                List<PromotionPlatform> allPromos = repository.findAll();
                for (PromotionPlatform other : allPromos) {
                    if (!other.getId().equals(promo.getId()) && other.isActive()) {
                        other.setActive(false);
                        repository.save(other);
                    }
                }
            }

            return Optional.of(repository.save(promo));
        }
        return Optional.empty();
    }


    public boolean deletePromotion(String promotionId) {
        Optional<PromotionPlatform> optionalPromotion = repository.findById(promotionId);
        if (optionalPromotion.isPresent()) {
            repository.delete(promotionId);
            return true;
        }
        return false;
    }

    public Page<PromotionPlatform> getAllPromotions(PromotionType type, Pageable pageable) {
        return (type != null)
                ? repository.findAllByType(type, pageable)
                : repository.findAll(pageable);
    }

    public Optional<PromotionPlatform> findById(String promotionId) {
        return repository.findById(promotionId);
    }

    public List<PromotionPlatform> getExpiredPromotions() {
        return repository.findAll().stream()
                .filter(p -> p.getEndDate().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    public Optional<PromotionPlatform> updatePromotion(String id, PromotionPlatform updated) {
        return repository.findById(id).map(existing -> {
            // Only update fields if the new value is not null
            if (updated.getTitle() != null) existing.setTitle(updated.getTitle());
            if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
            if (updated.getType() != null) existing.setType(updated.getType());
            if (updated.getDiscountValue() != 0.0) existing.setDiscountValue(updated.getDiscountValue());
            if (updated.getStartDate() != null) existing.setStartDate(updated.getStartDate());
            if (updated.getEndDate() != null) existing.setEndDate(updated.getEndDate());

            // Handle active status only if it's explicitly set
            if (updated.isActive()) {
                // Deactivate all other promotions
                repository.findAll().stream()
                        .filter(p -> !p.getId().equals(id) && p.isActive())
                        .forEach(p -> {
                            p.setActive(false);
                            repository.save(p);
                        });
            }

            existing.setActive(updated.isActive());

            return repository.save(existing);
        });
    }
}
