package restaurant.infrastructure.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import restaurant.domain.model.Ingredient;
import restaurant.domain.repository.IngredientRepository;
import restaurant.infrastructure.Document.MongoIngredient;
import restaurant.infrastructure.mapper.IngredientMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class IngredientRepositoryImpl implements IngredientRepository {

    private final SpringDataIngredientRepository repository;

    @Autowired
    public IngredientRepositoryImpl(SpringDataIngredientRepository repository) {
        this.repository = repository;
    }

    @Override
    public Ingredient save(Ingredient ingredient) {
        // Convertir l'ingredient du domaine en MongoIngredient
        MongoIngredient mongoIngredient = IngredientMapper.toMongo(ingredient);
        MongoIngredient savedMongoIngredient = repository.save(mongoIngredient);
        // Convertir l'ingredient Mongo en objet domaine
        return IngredientMapper.toDomain(savedMongoIngredient);
    }

    @Override
    public Optional<Ingredient> findById(String id) {
        Optional<MongoIngredient> mongoIngredient = repository.findById(id);
        return mongoIngredient.map(IngredientMapper::toDomain);
    }

    @Override
    public List<Ingredient> findAll() {
        List<MongoIngredient> mongoIngredients = repository.findAll();
        return mongoIngredients.stream()
                .map(IngredientMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<Ingredient> findAllById(List<String> ids) {
        List<MongoIngredient> mongoIngredients = repository.findAllById(ids);
        return mongoIngredients.stream()
                .map(IngredientMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ingredient> findByCategoryId(String categoryId) {
        List<MongoIngredient> mongoIngredients = repository.findByCategoryId(categoryId);
        return mongoIngredients.stream()
                .map(IngredientMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ingredient> searchByNamePrefix(String q, int page, int size) {
        String anchored = "^" + q;                    // anchor on prefix
        var pageable    = PageRequest.of(page, size);
        return repository.findByNamePrefix(anchored, pageable)
                .stream()
                .map(IngredientMapper::toDomain)
                .toList();
    }
}
interface SpringDataIngredientRepository extends MongoRepository<MongoIngredient, String> {
    List<MongoIngredient> findByCategoryId(String categoryId);  // Correction ici

    /**
     * Case-insensitive “starts with”:  ^<query>.*  (anchored)
     */
    @Query(value   = "{ 'name': { $regex : ?0, $options: 'i' } }",
            fields  = "{ '_id': 1, 'name': 1, 'category': 1, 'restaurantId': 1,'createdby': 1 }")
    List<MongoIngredient> findByNamePrefix(String anchoredRegex, org.springframework.data.domain.Pageable pageable);
}