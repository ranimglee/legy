package shared.port;

import shared.dto.ProductCategoryInfo;
import shared.dto.ProductInfo;

import java.util.Collection;
import java.util.Map;

public interface ProductQueryPort {
    ProductInfo getProductById(String productId);
    Map<String, ProductCategoryInfo> getCategoryInfoForProducts(Collection<String> productIds);

}