package user.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import user.application.dto.in.*;
import user.application.dto.out.*;
import user.application.service.*;
import user.domain.model.UserEntity;
import user.domain.service.UserService;
import user.infrastructure.security.JwtUtilImpl;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Registration & Verification", description = "Endpoints for registering different user roles and verifying email addresses")

public class UserController {

    private final ClientService clientService ;
    private final LivreurService livreurService ;
    private final RestaurantManagerService restaurantManagerService ;
    private final ModerateurService moderateurService ;

    private final UserService userService;


    private final FinancierService financierService;

    public UserController(ClientService clientService, LivreurService livreurService, RestaurantManagerService restaurantManagerService, ModerateurService moderateurService, UserService userService, FinancierService financierService) {
        this.clientService = clientService;
        this.livreurService = livreurService;
        this.restaurantManagerService = restaurantManagerService;
        this.moderateurService = moderateurService;
        this.userService = userService;

        this.financierService = financierService;
    }

    @Operation(
            summary = "Register a new client",
            description = "Registers a new user with the CLIENT role."
    )
    @PostMapping("/register-client")
    public ResponseEntity<ClientResponse> registerClient(@Valid @RequestBody RegisterClientRequest request) {
        return ResponseEntity.ok(clientService.registerClient(request));
    }
    @Operation(
            summary = "Register a new livreur",
            description = "Registers a new user with the LIVREUR role."
    )
    @PostMapping("/register-livreur")
    public ResponseEntity<LivreurResponse> registerLivreur(@Valid @RequestBody RegisterLivreurRequest request) {
        LivreurResponse response = livreurService.registerLivreur(request);
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Register a restaurant manager",
            description = "Registers a new user with the RESTAURANT_MANAGER role."
    )
    @PostMapping("/register-restaurant-manager")
    public ResponseEntity<RestaurantManagerResponse> registerRestaurantManager(
            @Valid @RequestBody RegisterRestaurantManagerRequest request) {
        return ResponseEntity.ok(restaurantManagerService.registerRestaurantManager(request));
    }
    @Operation(
            summary = "Register a moderator",
            description = "Registers a new user with the MODERATEUR role."
    )
    @PostMapping("/register-moderateur")
    public ResponseEntity<ModerateurResponse> registerModerateur(
            @Valid @RequestBody RegisterModerateurRequest request) {
        return ResponseEntity.ok(moderateurService.registerModerateur(request));
    }

    @Operation(
            summary = "Register a financier",
            description = "Registers a new user with the FINANCIER role."
    )
    @PostMapping("/register-financier")
    public ResponseEntity<FinancierResponse> registerFinancier(
            @Valid @RequestBody RegisterFinancierRequest request) {
        return ResponseEntity.ok(financierService.registerFinancier(request));
    }


    @Operation(
            summary = "Verify user email",
            description = "Verifies a user's email address using a token sent by email."
    )
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        boolean verified = clientService.verifyEmail(token);
        if (verified) {
            return ResponseEntity.ok("Email successfully verified! You can now log in.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        }
    }

    @GetMapping("/moderateur/get-all-non-moderateurs")
    public ResponseEntity<List<UserEntity>> getAllNonModerateurUsers() {
        List<UserEntity> users = userService.findAllNonModerateurUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/moderateur/total-connected-user-per-role")
    public ResponseEntity<Map<String, Long>> getConnectedUserSummary() {
        return ResponseEntity.ok(userService.getConnectedUserSummary());
    }

    @GetMapping("/moderateur/active/count")
    public ResponseEntity<Map<String, Long>> getActiveUserCount() {
        long count = userService.getTotalActiveUsers();
        return ResponseEntity.ok(Map.of("activeUsers", count));
    }
    @GetMapping("/moderateur/get-user-info/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/moderateur/delete-user/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable String id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/moderateur/ban-user/{userId}")
    public ResponseEntity<String> banUser(@PathVariable String userId) {
        userService.banUserById(userId);
        return ResponseEntity.ok("User has been banned.");
    }
    @GetMapping("/livreur/evolution/monthly")
    @PreAuthorize("hasRole('MODERATEUR')")
    public ResponseEntity<Map<String, Long>> getMonthlyManagerEvolution() {
        Map<String, Long> monthlyEvolution = livreurService.getMonthlyLivreurEvolution();
        return ResponseEntity.ok(monthlyEvolution);
    }
}