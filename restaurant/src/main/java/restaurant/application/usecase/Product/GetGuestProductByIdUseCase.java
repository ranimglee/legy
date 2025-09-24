package restaurant.application.usecase.Product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Product.GuestProductResponseDTO;
import restaurant.domain.model.Product;
import restaurant.domain.service.ProductDomainService;

import java.util.Optional;

@Service
public class GetGuestProductByIdUseCase {

    private final ProductDomainService productDomainService;

    @Autowired
    public GetGuestProductByIdUseCase(ProductDomainService productDomainService) {
        this.productDomainService = productDomainService;
    }

    public GuestProductResponseDTO execute(String id) {
        Optional<Product> productOptional = productDomainService.getProductById(id);

        if (productOptional.isEmpty()) {
            throw new RuntimeException("Product not found with id: " + id);
        }

        Product product = productOptional.get();

        return new GuestProductResponseDTO(
                product.getName(),
                product.getDescription(),
                product.getPricePostCom(),
                product.getImageUrl(),
                product.getAvailability()

        );
    }
}

