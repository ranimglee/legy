package restaurant.application.usecase.Promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Product.ProductDTO;
import restaurant.application.dto.Promotion.PromotionRequestDTO;
import restaurant.application.dto.Promotion.PromotionResponseDTO;
import restaurant.domain.model.Promotion;
import restaurant.domain.repository.PromotionRepository;
import restaurant.domain.service.ProductQueryService;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class AddPromotionUseCase {

    private final PromotionRepository repo;
    private final ProductQueryService productQueryService;

    public PromotionResponseDTO execute(PromotionRequestDTO request, String restaurantId) {
        List<String> productIds = request.getProductIds();

        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Product IDs list is missing or empty in the request.");
        }

        boolean conflictExists = repo.findAll().stream().anyMatch(existing ->
                existing.getProductIds() != null &&
                        existing.getProductIds().stream().anyMatch(productIds::contains) &&
                        isActive(existing)
        );

        if (conflictExists) {
            throw new IllegalArgumentException("One or more products already have an active promotion.");
        }

        // Build your domain Promotion entity, setting all fields from request + restaurantId
        Promotion promo = Promotion.builder()
                .description(request.getDescription())
                .amount(request.getAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .productIds(productIds)
                .targetProductId(request.getTargetProductId())  // IMPORTANT to set this!
                .restaurantId(restaurantId)
                .imageUrl(request.getImageUrl())  // set image if applicable
                .build();

        promo = repo.save(promo);

        // Fetch full ProductDTOs to populate response
        List<ProductDTO> productDTOs = productQueryService.getProductsByIds(productIds);

        return PromotionResponseDTO.builder()
                .id(promo.getId())
                .description(promo.getDescription())
                .amount(promo.getAmount())
                .startDate(promo.getStartDate())
                .endDate(promo.getEndDate())
                .productIds(productIds) // optionally include if you want IDs too
                .products(productDTOs)  // full product details
                .targetProductId(promo.getTargetProductId())  // set targetProductId
                .imageUrl(promo.getImageUrl())                // set image URL
                .restaurantId(promo.getRestaurantId())
                .build();
    }

    private boolean isActive(Promotion promo) {
        LocalDateTime now = LocalDateTime.now();
        return (promo.getStartDate() == null || !promo.getStartDate().isAfter(now)) &&
                (promo.getEndDate() == null || !promo.getEndDate().isBefore(now));
    }
}
