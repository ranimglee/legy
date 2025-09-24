package restaurant.application.usecase.Product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import restaurant.application.repository.ListGuestProductsUseCase;
import restaurant.domain.model.Product;
import restaurant.domain.model.ProductFilterCriteria;
import restaurant.domain.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ListGuestProductsService implements ListGuestProductsUseCase {

    private final ProductRepository productRepository;

    @Override
    public Page<Product> handle(ProductFilterCriteria criteria) {
        return productRepository.findForGuest(criteria);
    }
}
