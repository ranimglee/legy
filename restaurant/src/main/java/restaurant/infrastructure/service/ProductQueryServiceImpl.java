package restaurant.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Product.ProductDTO;
import restaurant.domain.model.Product;
import restaurant.domain.repository.ProductRepository;
import restaurant.domain.service.ProductQueryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductQueryServiceImpl implements ProductQueryService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductDTO> getProductsByIds(List<String> ids) {
        List<Product> products = productRepository.findAllByIds(ids);
        return products.stream()
                .map(product -> new ProductDTO(
                        product.getId(),
                        product.getName(),
                        product.getPricePostCom(),
                        product.getPricePreCom(),
                        product.getStatus(),
                        product.getImageUrl()
                ))
                .collect(Collectors.toList());
    }
}
