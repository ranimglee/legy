package restaurant.application.usecase.Promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Product.ProductDTO;
import restaurant.application.dto.Promotion.PromotionResponseDTO;
import restaurant.domain.model.Promotion;
import restaurant.domain.repository.PromotionRepository;
import restaurant.domain.service.ProductQueryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPromotionUseCase {
    private final PromotionRepository repo;
    private final ProductQueryService productQueryService;

    public PromotionResponseDTO byId(String id) {
        Promotion p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));

        return map(p);
    }

    private PromotionResponseDTO map(Promotion p) {
        List<ProductDTO> products = productQueryService.getProductsByIds(p.getProductIds());

        return PromotionResponseDTO.builder()
                .id(p.getId())
                .description(p.getDescription())
                .amount(p.getAmount())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .products(products)
                .targetProductId(p.getTargetProductId())
                .restaurantId(p.getRestaurantId())
                .build();
    }
}
