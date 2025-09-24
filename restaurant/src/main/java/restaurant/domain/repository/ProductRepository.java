package restaurant.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import restaurant.domain.model.Product;
import restaurant.domain.model.ProductFilterCriteria;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    List<Product> findByRestaurantId(String restaurantId);

    Optional<Product> findById(String id);

    List<Product> findAll();

    void deleteById(String id);

    List<Product> findByCategoryId(String categoryId);

    Page<Product> findForGuest(ProductFilterCriteria criteria);

  Page<Product> findByRestaurantIds(Collection<String> restaurantIds,
                                      Pageable pageable);

   Page<Product> findPageByRestaurantId(String restaurantId, Pageable pageable);

    List<Product> findAllByIds(Collection<String> ids);
    List<Product> findByRestaurantIdAndCategoryId(
            String restaurantId,
            String categoryId
    );

    boolean existsById(String productId);
}
