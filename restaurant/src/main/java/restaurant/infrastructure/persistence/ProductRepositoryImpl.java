package restaurant.infrastructure.persistence;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import restaurant.domain.model.Product;
import restaurant.domain.model.ProductFilterCriteria;
import restaurant.domain.repository.ProductRepository;
import restaurant.infrastructure.Document.MongoProduct;
import restaurant.infrastructure.mapper.ProductMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final SpringDataProductRepository repository;
    private final MongoTemplate mongoTemplate;

    private final ProductMapper productMapper;

    @Override
    public List<Product> findByRestaurantIdAndCategoryId(String restaurantId, String categoryId) {
        return repository
                .findByRestaurantIdAndCategoryId(restaurantId, categoryId)
                .stream()
                .map(productMapper::toDomain) // ✅ utiliser le mapper injecté
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(String productId) {
        return repository.existsById(productId);
    }

    @Override
    public Product save(Product product) {
        MongoProduct mongoProduct = productMapper.toMongo(product);
        MongoProduct savedMongo = repository.save(mongoProduct);
        return productMapper.toDomain(savedMongo);
    }

    @Override
    public Optional<Product> findById(String id) {
        return repository.findById(id)
                .map(productMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return repository.findAll().stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<Product> findByCategoryId(String categoryId) {
        return repository.findByCategoryId(categoryId).stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Product> findForGuest(ProductFilterCriteria criteria) {
        List<Criteria> filters = new ArrayList<>();

        // ✅ Utilise categoryId simple au lieu de category.$id
        criteria.getCategoryId().ifPresent(cat -> filters.add(Criteria.where("categoryId").is(cat)));
        criteria.getMinPrice().ifPresent(min -> filters.add(Criteria.where("pricePreCom").gte(min)));
        criteria.getMaxPrice().ifPresent(max -> filters.add(Criteria.where("pricePreCom").lte(max)));
        criteria.getKeyword().ifPresent(keyword ->
                filters.add(new Criteria().orOperator(
                        Criteria.where("name").regex(keyword, "i"),
                        Criteria.where("description").regex(keyword, "i")
                ))
        );

        Query query = new Query();
        if (!filters.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(filters.toArray(new Criteria[0])));
        }

        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize());

        criteria.getSortBy().ifPresent(sortField ->
                query.with(Sort.by(Sort.Direction.ASC, sortField))
        );

        query.with(pageable);

        List<MongoProduct> mongoProducts = mongoTemplate.find(query, MongoProduct.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), MongoProduct.class);

        List<Product> domainProducts = mongoProducts.stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());

        return new PageImpl<>(domainProducts, pageable, total);
    }
    @Override
    public Page<Product> findByRestaurantIds(Collection<String> restaurantIds, Pageable pageable) {
        return repository
                .findByRestaurantIdIn(restaurantIds, pageable)
                .map(productMapper::toDomain);
    }

    @Override
    public List<Product> findByRestaurantId(String restaurantId) {
        return repository.findByRestaurantId(restaurantId)
                .stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());
    }

   @Override
    public Page<Product> findPageByRestaurantId(String restaurantId, Pageable pageable) {
        Page<MongoProduct> mongoPage = repository.findByRestaurantId(restaurantId, pageable);
        return ProductMapper.toDomainPage(mongoPage, pageable);
    }


    @Override
    public List<Product> findAllByIds(Collection<String> ids) {
        return repository.findAllById(ids).stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());
    }
}

@Repository

interface SpringDataProductRepository extends MongoRepository<MongoProduct, String> {
    List<MongoProduct> findByCategoryId(String categoryId);

    Page<MongoProduct> findByRestaurantIdIn(
            Collection<String> restaurantIds,
            Pageable pageable
    );

    List<MongoProduct> findByRestaurantIdAndCategoryId(
            String restaurantId,
            String categoryId
    );

    List<MongoProduct> findByRestaurantId(String restaurantId); // Non-paginated
    Page<MongoProduct> findByRestaurantId(String restaurantId, Pageable pageable); // Paginated

}