package restaurant.infrastructure.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import restaurant.domain.model.Category;
import restaurant.domain.repository.CategoryRepository;
import restaurant.infrastructure.Document.MongoCategory;
import restaurant.infrastructure.mapper.CategoryMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final SpringDataCategoryRepository repository;

    @Autowired
    public CategoryRepositoryImpl(SpringDataCategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Category save(Category category) {
        MongoCategory mongoCategory = CategoryMapper.toMongo(category);
        MongoCategory saved = repository.save(mongoCategory);
        return CategoryMapper.toDomain(saved);
    }



    @Override
    public Optional<Category> findById(String id) {
        return repository.findById(id)
                .map(CategoryMapper::toDomain);
    }
    @Override
    public List<Category> findAll() {
        List<MongoCategory> mongoCategories = repository.findAll();
        return mongoCategories.stream()
                .map(CategoryMapper::toDomain)
                .collect(Collectors.toList());
    }


    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<Category> findTopN(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<MongoCategory> mongoCategories = repository.findAllWithLimit(pageable);
        return mongoCategories.stream()
                .map(CategoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Category> searchByNamePrefix(String q, int page, int size) {
        String anchored = "^" + q;                     // anchor for prefix search
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByNamePrefix(anchored, pageable)
                .stream()
                .map(CategoryMapper::toDomain)
                .toList();
    }

}

interface SpringDataCategoryRepository extends MongoRepository<MongoCategory, String> {

    @Query("{}")
    List<MongoCategory> findAllWithLimit(Pageable pageable);

    @Query(
            value  = "{ 'name': { $regex : ?0, $options: 'i' } }",
            fields = "{ '_id': 1, 'name': 1, 'createdby': 1, " +
                    "          'restaurantId': 1 }"
    )
    List<MongoCategory> findByNamePrefix(String anchoredRegex, Pageable pageable);
}
