package restaurant.application.usecase.Restaurant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import restaurant.application.dto.Restaurant.AddAvisRequestDTO;
import restaurant.application.dto.Restaurant.AvisResponseDTO;
import restaurant.domain.model.Avis;
import restaurant.domain.service.AvisDomainService;
import shared.events.UserRecommendationProducer;

@Component
@RequiredArgsConstructor
public class AddAvisUseCase {
    private final AvisDomainService avisDomainService;
    private final UserRecommendationProducer userRecommendationProducer;


    /**
     * Inserts or updates a user’s avis + updates the restaurant’s aggregates.
     *
     * @param restaurantId the target restaurant
     * @param userId       extracted from the JWT in your controller
     * @param req          contains score & comment
     * @return the saved or updated avis as a DTO
     */
    public AvisResponseDTO execute(String restaurantId, String userId, AddAvisRequestDTO req) {
        Avis saved = avisDomainService.addOrUpdateAvis(
                restaurantId,
                userId,
                req.getScore(),
                req.getComment()
        );
        userRecommendationProducer.sendUserRecommendationEvent(userId);
        return new AvisResponseDTO(
                saved.getId(),
                saved.getUserId(),
                saved.getScore(),
                saved.getComment(),
                saved.getCreatedAt()
        );
    }
}
