package restaurant.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.domain.model.Menu;
import restaurant.domain.repository.MenuRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuDomainService {

    private final MenuRepository menuRepository;

    public Menu addMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    public Optional<Menu> getMenuById(String id) {
        return menuRepository.findById(id);
    }

    public void deleteMenu(String id) {
        menuRepository.deleteById(id);
    }
}
