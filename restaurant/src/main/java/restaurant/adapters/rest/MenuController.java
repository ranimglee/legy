package restaurant.adapters.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import restaurant.application.dto.Menu.MenuRequestDTO;
import restaurant.application.dto.Menu.MenuResponseDTO;
import restaurant.application.usecase.Menu.*;
import restaurant.config.JwtConfig;

import java.util.List;

@RestController
@RequestMapping("/menus")
public class MenuController {

    private final AddMenuUseCase addMenuUseCase;
    private final GetAllMenusUseCase getAllMenusUseCase;
    private final GetMenuByIdUseCase getMenuByIdUseCase;
    private final GetStructuredMenuUseCase getStructuredMenuUseCase;
    private final DeleteMenuUseCase deleteMenuUseCase;
    private final UpdateMenuUseCase updateMenuUseCase;
    private final JwtConfig jwtConfig;



    @Autowired
    public MenuController(AddMenuUseCase addMenuUseCase,
                          GetAllMenusUseCase getAllMenusUseCase,
                          GetMenuByIdUseCase getMenuByIdUseCase,
                          GetStructuredMenuUseCase getStructuredMenuUseCase,
                          DeleteMenuUseCase deleteMenuUseCase,
                          UpdateMenuUseCase updateMenuUseCase,
                           final JwtConfig jwtConfig
) {
        this.addMenuUseCase = addMenuUseCase;
        this.getAllMenusUseCase = getAllMenusUseCase;
        this.getMenuByIdUseCase = getMenuByIdUseCase;
        this.getStructuredMenuUseCase = getStructuredMenuUseCase;
        this.deleteMenuUseCase = deleteMenuUseCase;
        this.updateMenuUseCase=updateMenuUseCase;
        this.jwtConfig = jwtConfig;
    }

    @PostMapping
    public MenuResponseDTO addMenu(@Valid @RequestBody MenuRequestDTO request,
                                   HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);  // remove "Bearer " prefix
            String userId = jwtConfig.extractUserIdFromAccessToken(token);
            request.setCreatedby(userId);
            System.out.println("Extracted UserId from JWT: " + userId);
        } else {
            throw new RuntimeException("JWT token missing or invalid.");
        }
        return addMenuUseCase.execute(request);
    }

    @GetMapping
    public List<MenuResponseDTO> getAllMenus() {
        return getAllMenusUseCase.execute();
    }

    @GetMapping("/{id}")
    public MenuResponseDTO getMenuById(@PathVariable String id) {
        return getMenuByIdUseCase.execute(id);
    }

    @GetMapping("/{id}/structured")
    public MenuResponseDTO getStructuredMenu(@PathVariable String id) {
        return getStructuredMenuUseCase.execute(id);
    }
    @PutMapping("/{id}")
    public MenuResponseDTO updateMenu(@PathVariable String id, @Valid   @RequestBody MenuRequestDTO request) {
        return updateMenuUseCase.execute(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteMenu(@PathVariable String id) {
        deleteMenuUseCase.execute(id);
    }
}
