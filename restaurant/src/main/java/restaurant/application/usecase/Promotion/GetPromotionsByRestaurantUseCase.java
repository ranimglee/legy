package restaurant.application.usecase.Promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Product.ProductDTO;
import restaurant.application.dto.Promotion.PromotionResponseDTO;
import restaurant.domain.model.Promotion;
import restaurant.domain.repository.PromotionRepository;
import restaurant.domain.service.ProductQueryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class   GetPromotionsByRestaurantUseCase {

    private final PromotionRepository promotionRepository;
    private final ProductQueryService productQueryService;

    public List<PromotionResponseDTO> execute(String restaurantId) {
        List<Promotion> promotions = promotionRepository.findByRestaurantId(restaurantId);

        return promotions.stream()
                .map(promo -> {
                    List<ProductDTO> products = productQueryService.getProductsByIds(promo.getProductIds());

                    return PromotionResponseDTO.builder()
                            .id(promo.getId())
                            .description(promo.getDescription())
                            .amount(promo.getAmount())
                            .startDate(promo.getStartDate())
                            .endDate(promo.getEndDate())
                            .products(products)
                            .restaurantId(promo.getRestaurantId())
                            .imageUrl(promo.getImageUrl())
                            .targetProductId(promo.getTargetProductId())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
