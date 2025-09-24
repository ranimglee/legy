package restaurant.application.usecase.Menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Menu.MenuResponseDTO;
import restaurant.domain.model.Menu;
import restaurant.domain.service.MenuDomainService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllMenusUseCase {

    private final MenuDomainService menuDomainService;

    public List<MenuResponseDTO> execute() {
        return menuDomainService.getAllMenus().stream()
                .map(m -> new MenuResponseDTO(m.getId(), m.getName(), m.getDescription(), null,m.getRestaurantId()))
                .collect(Collectors.toList());
    }
}
