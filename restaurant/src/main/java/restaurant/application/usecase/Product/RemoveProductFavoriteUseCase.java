package restaurant.application.usecase.Product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.exception.ProductFavoriteNotFoundException;
import restaurant.domain.repository.ProductFavoriteRepository;

@Service
@RequiredArgsConstructor
public class RemoveProductFavoriteUseCase {

    private final ProductFavoriteRepository favRepo;

    public void execute(String userId, String productId) {
        favRepo.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ProductFavoriteNotFoundException(userId, productId));
        favRepo.deleteByUserIdAndProductId(userId, productId);
    }
}
