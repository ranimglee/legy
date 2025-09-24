package restaurant.application.usecase.Product;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Product.ProductFavoriteStatusDTO;
import restaurant.domain.repository.ProductFavoriteRepository;

@Service
@RequiredArgsConstructor
public class CheckProductFavoriteStatusUseCaseImpl implements CheckProductFavoriteStatusUseCase {

    private final ProductFavoriteRepository repository;

    @Override
    public ProductFavoriteStatusDTO execute(String userId, String productId) {
        boolean exists = repository.findByUserIdAndProductId(userId, productId).isPresent();
        return new ProductFavoriteStatusDTO(exists);
    }
}

