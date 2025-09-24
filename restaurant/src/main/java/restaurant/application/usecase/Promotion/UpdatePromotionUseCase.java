package restaurant.application.usecase.Promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Product.ProductDTO;
import restaurant.application.dto.Promotion.PromotionRequestDTO;
import restaurant.application.dto.Promotion.PromotionResponseDTO;
import restaurant.domain.model.Promotion;
import restaurant.domain.repository.PromotionRepository;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class UpdatePromotionUseCase {

    private final PromotionRepository repo;

    public PromotionResponseDTO execute(String id, PromotionRequestDTO request) {
        Promotion promo = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));

        // Extraction directe des productIds, car dans PromotionRequestDTO tu as List<String> productIds
        List<String> productIds = request.getProductIds();

        promo.setDescription(request.getDescription());
        promo.setAmount(request.getAmount());
        promo.setStartDate(request.getStartDate());
        promo.setEndDate(request.getEndDate());
        promo.setProductIds(productIds);
        promo.setTargetProductId(request.getTargetProductId());
        promo.setRestaurantId(request.getRestaurantId());

        repo.save(promo);

        // Si tu veux renvoyer les produits complets dans la réponse, charge-les via ProductQueryService (optionnel)
        // Ici on renvoie juste les productIds car pas de ProductDTO dans le request
        return PromotionResponseDTO.builder()
                .id(promo.getId())
                .description(promo.getDescription())
                .amount(promo.getAmount())
                .startDate(promo.getStartDate())
                .endDate(promo.getEndDate())
                .productIds(productIds)
                .targetProductId(promo.getTargetProductId())
                .restaurantId(promo.getRestaurantId())
                .build();
    }
}
