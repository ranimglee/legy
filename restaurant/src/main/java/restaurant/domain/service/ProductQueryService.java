package restaurant.domain.service;

import restaurant.application.dto.Product.ProductDTO;

import java.util.List;

public interface ProductQueryService {
    List<ProductDTO> getProductsByIds(List<String> ids);
}
