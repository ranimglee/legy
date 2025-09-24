package restaurant.infrastructure.adapter;

import org.springframework.stereotype.Component;
import restaurant.domain.model.Product;
import restaurant.domain.repository.ProductRepository;
import restaurant.domain.repository.SupplementRepository;
import shared.dto.ProductCategoryInfo;
import shared.dto.ProductInfo;
import shared.dto.SupplementDTO;
import shared.port.ProductQueryPort;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProductQueryAdapter implements ProductQueryPort {

    private final ProductRepository productRepository;
    private final SupplementRepository supplementRepository;

    public ProductQueryAdapter(ProductRepository productRepository,
                               SupplementRepository supplementRepository) {
        this.productRepository = productRepository;
        this.supplementRepository = supplementRepository;
    }

    @Override
    public ProductInfo getProductById(String productId) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<SupplementDTO> supplementDTOs = List.of();
        if (p.getSupplementIds() != null && !p.getSupplementIds().isEmpty()) {
            var supplements = supplementRepository.findAllById(p.getSupplementIds());
            supplementDTOs = supplements.stream()
                    .map(s -> new SupplementDTO(s.getId(), s.getName(), s.getPrice()))
                    .collect(Collectors.toList());
        }

        return new ProductInfo(
                p.getId(),
                p.getName(),
                p.getPricePreCom(),
                p.getPricePostCom(),
                p.getImageUrl(),
                p.getAvailability(),
                supplementDTOs
        );
    }

    @Override
    public Map<String, ProductCategoryInfo> getCategoryInfoForProducts(Collection<String> productIds) {
        return productRepository.findAllByIds(productIds).stream()
                .collect(Collectors.toMap(
                        Product::getId,
                        p -> {
                            var category = p.getCategory();
                            return new ProductCategoryInfo(
                                    p.getId(),
                                    category != null ? category.getId() : null,
                                    category != null ? category.getName() : null
                            );
                        }
                ));
    }
}
