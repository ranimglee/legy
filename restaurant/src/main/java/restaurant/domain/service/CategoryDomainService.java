package restaurant.domain.service;

import org.springframework.stereotype.Service;
import restaurant.domain.model.Category;
import restaurant.domain.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryDomainService {

    private final CategoryRepository categoryRepository;

    public CategoryDomainService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }


    public Optional<Category> getCategoryById(String id) {
        return categoryRepository.findById(id);
    }
    public Category updateCategory(Category category) {
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }


    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }

    public List<Category> getTopCategories(int limit) {
        return categoryRepository.findTopN(limit);
    }
    public List<Category> searchByNamePrefix(String q, int page, int size) {
        return categoryRepository.searchByNamePrefix(q, page, size);
    }

}
