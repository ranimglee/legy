package restaurant.application.usecase.Product;

import restaurant.application.dto.Product.ProductFavoriteStatusDTO;

public interface CheckProductFavoriteStatusUseCase {
    ProductFavoriteStatusDTO execute(String userId, String productId);
}

