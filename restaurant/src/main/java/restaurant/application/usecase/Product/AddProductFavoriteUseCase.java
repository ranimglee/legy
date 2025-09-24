package restaurant.application.usecase.Product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.exception.DuplicateProductFavoriteException;
import restaurant.application.exception.ProductNotFoundException;
import restaurant.domain.model.ProductFavorite;
import restaurant.domain.repository.ProductFavoriteRepository;
import restaurant.domain.service.ProductDomainService;

@Service
@RequiredArgsConstructor
public class AddProductFavoriteUseCase {

    private final ProductFavoriteRepository favRepo;
    private final ProductDomainService productService;

    public void execute(String userId, String productId) {
        productService.getProductById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        favRepo.findByUserIdAndProductId(userId, productId)
                .ifPresent(f -> {
                    throw new DuplicateProductFavoriteException(userId, productId);
                });

        ProductFavorite fav = new ProductFavorite();
        fav.setUserId(userId);
        fav.setProductId(productId);
        favRepo.save(fav);
    }
}
