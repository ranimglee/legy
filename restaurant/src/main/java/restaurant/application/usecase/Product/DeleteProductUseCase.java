package restaurant.application.usecase.Product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.domain.service.ProductDomainService;

@Service
@RequiredArgsConstructor
public class DeleteProductUseCase {

    private final ProductDomainService productDomainService;

    public void execute(String id) {
        productDomainService.deleteProduct(id);
    }
}