package restaurant.application.repository;

import org.springframework.data.domain.Page;
import restaurant.domain.model.Product;
import restaurant.domain.model.ProductFilterCriteria;

public interface ListGuestProductsUseCase {
    Page<Product> handle(ProductFilterCriteria criteria);
}