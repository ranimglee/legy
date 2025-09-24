package restaurant.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurant.domain.model.Avis;
import restaurant.domain.repository.AvisRepository;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvisDomainService {

    private final AvisRepository avisRepository;
    private final RestaurantDomainService restaurantDomainService;

    @Transactional
    public Avis addOrUpdateAvis(String restaurantId, String userId, int score, String comment) {
        var existingOpt = avisRepository.findByRestaurantIdAndUserId(restaurantId, userId);

        if (existingOpt.isPresent()) {
            // — override path —
            Avis existing = existingOpt.get();
            int oldScore = existing.getScore();

            // 1) update restaurant aggregates
            restaurantDomainService.replaceRating(restaurantId, oldScore, score);

            // 2) update the avis record
            existing.setScore(score);
            existing.setComment(comment);

            return avisRepository.save(existing);
        } else {
            // — first‑time path —
            // 1) bump aggregates
            restaurantDomainService.rateRestaurant(restaurantId, score);

            // 2) create new avis
            Avis newAvis = new Avis();
            newAvis.setRestaurantId(restaurantId);
            newAvis.setUserId(userId);
            newAvis.setScore(score);
            newAvis.setComment(comment);
            newAvis.setCreatedAt(Instant.now());
            return avisRepository.save(newAvis);
        }
    }

    public List<Avis> getAvisForRestaurant(String restaurantId) {
        return avisRepository.findByRestaurantId(restaurantId);
    }
    public Page<Avis> getPagedAvisForRestaurant(String restaurantId, Pageable pageable) {
        return avisRepository.findByRestaurantId(restaurantId, pageable);
    }


}
