package restaurant.application.usecase.Menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.domain.service.MenuDomainService;

@Service
@RequiredArgsConstructor
public class DeleteMenuUseCase {

    private final MenuDomainService menuDomainService;

    public void execute(String id) {
        menuDomainService.deleteMenu(id);
    }
}
