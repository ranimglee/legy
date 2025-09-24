package restaurant.infrastructure.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import restaurant.domain.model.Menu;
import restaurant.domain.repository.MenuRepository;
import restaurant.infrastructure.Document.MongoMenu;
import restaurant.infrastructure.mapper.MenuMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MenuRepositoryImpl implements MenuRepository {

    private final SpringDataMenuRepository repository;

    @Autowired
    public MenuRepositoryImpl(SpringDataMenuRepository repository) {
        this.repository = repository;
    }

    @Override
    public Menu save(Menu menu) {
        MongoMenu savedMongo = repository.save(MenuMapper.toMongo(menu));
        return MenuMapper.toDomain(savedMongo);
    }

    @Override
    public List<Menu> findAll() {
        return repository.findAll()
                .stream()
                .map(MenuMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Menu> findById(String id) {
        return repository.findById(id).map(MenuMapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
interface SpringDataMenuRepository extends MongoRepository<MongoMenu, String> {
}