package restaurant.application.usecase.Menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Menu.MenuResponseDTO;
import restaurant.domain.model.Menu;
import restaurant.domain.service.MenuDomainService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetMenuByIdUseCase {

    private final MenuDomainService menuDomainService;

    public MenuResponseDTO execute(String id) {
        Optional<Menu> menuOpt = menuDomainService.getMenuById(id);
        if (menuOpt.isEmpty()) {
            throw new RuntimeException("Menu not found with id: " + id);
        }

        Menu m = menuOpt.get();
        return new MenuResponseDTO(m.getId(), m.getName(), m.getDescription(), null,m.getRestaurantId());
    }
}
