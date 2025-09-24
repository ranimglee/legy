package restaurant.application.usecase.Product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Product.FavoriteProductDTO;
import restaurant.application.dto.Restaurant.PagedResponseDTO;
import restaurant.application.exception.ProductNotFoundException;
import restaurant.domain.repository.ProductFavoriteRepository;
import restaurant.domain.service.ProductDomainService;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetProductFavoritesUseCase {

    private final ProductFavoriteRepository favRepo;
    private final ProductDomainService productService;

    public PagedResponseDTO<FavoriteProductDTO> execute(String userId, int page, int size) {
        var pr = PageRequest.of(page, size);
        var favPage = favRepo.findByUserId(userId, pr);

        var dtos = favPage.getContent().stream()
                .map(fav -> {
                    var productId = fav.getProductId();
                    var productOpt = productService.getProductById(productId);

                    if (productOpt.isEmpty()) {
                        log.warn("⚠️ Product not found for favoriteId={} and productId={}", fav.getId(), productId);
                        throw new ProductNotFoundException("Product not found with id: " + productId);
                    }

                    var p = productOpt.get();
                    return new FavoriteProductDTO(
                            p.getId(),
                            p.getName(),
                            p.getImageUrl(),
                            p.getPricePostCom(),
                            p.getRestaurantId()
                    );
                })
                .toList();

        return new PagedResponseDTO<>(
                dtos,
                favPage.getNumber(),
                favPage.getSize(),
                favPage.getTotalElements(),
                favPage.getTotalPages()
        );
    }
}
