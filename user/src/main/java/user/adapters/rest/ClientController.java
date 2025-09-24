package user.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import user.application.dto.in.AddToCartRequest;
import user.application.dto.in.ClientProfileResponse;
import user.application.dto.out.CartResponseDTO;
import user.application.dto.out.UpdateClientProfileRequest;
import user.application.service.CartService;
import user.application.service.ClientService;
import user.infrastructure.security.JwtUtilImpl;

import java.util.Map;

@PreAuthorize("hasRole('CLIENT')")
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Client", description = "Endpoints for client profile and cart operations")
@Slf4j
public class ClientController {
    private final ClientService clientService;
    private final JwtUtilImpl jwtUtil;
    private final CartService cartService;

    @Operation(summary = "Get authenticated client profile", description = "Returns the profile of the currently authenticated client.")
    @GetMapping("/me")
    public ResponseEntity<ClientProfileResponse> getClientProfile(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmailFromAccessToken(token);

        ClientProfileResponse profile = clientService.getClientByEmail(email);
        return ResponseEntity.ok(profile);
    }

    @Operation(summary = "Update client profile", description = "Updates the profile information of the authenticated client.")
    @PutMapping("/me/update")
    public ResponseEntity<ClientProfileResponse> updateMyProfile(
            @RequestBody UpdateClientProfileRequest request,
            HttpServletRequest httpRequest
    ) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmailFromAccessToken(token);

        return ResponseEntity.ok(clientService.updateProfile(email, request));
    }
    @Operation(summary = "Get client cart", description = "Returns the current shopping cart of the authenticated client.")
    @GetMapping("/cart/client")
    public ResponseEntity<CartResponseDTO> getClientCart(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);
        String userId = jwtUtil.extractUserIdFromAccessToken(token);

        CartResponseDTO cart = cartService.getClientCart(userId);
        return ResponseEntity.ok(cart);
    }
    @Operation(summary = "Add product to cart", description = "Adds a product to the authenticated client's cart.")

    @PostMapping("/cart/add")
    public ResponseEntity<CartResponseDTO> addToClientCart(
            @RequestBody AddToCartRequest request,
            HttpServletRequest httpRequest
    ) {
        String token = httpRequest.getHeader("Authorization").substring(7);
        String userId = jwtUtil.extractUserIdFromAccessToken(token);

        CartResponseDTO updatedCart = cartService.addToClientCart(userId, request);
        return ResponseEntity.ok(updatedCart);
    }

    @Operation(summary = "Remove product from cart", description = "Removes a specific product from the client's cart.")
    @DeleteMapping("/cart/remove/{productId}")
    public ResponseEntity<CartResponseDTO> removeFromCart(
            @PathVariable String productId,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);
        String userId = jwtUtil.extractUserIdFromAccessToken(token);


        CartResponseDTO updatedCart = cartService.removeProductFromClientCart(userId, productId);
        return ResponseEntity.ok(updatedCart);
    }

    @Operation(summary = "Clear client cart", description = "Removes all products from the client's cart.")

    @DeleteMapping("/cart/clear")
    public ResponseEntity<Void> clearCart(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String userId = jwtUtil.extractUserIdFromAccessToken(token);


        cartService.clearClientCart(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Decrease product quantity", description = "Decreases the quantity of a product in the client's cart by 1.")

    @PutMapping("/cart/decrease/{productId}")
    public ResponseEntity<CartResponseDTO> decreaseProductQuantity(
            @PathVariable String productId,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);
        String userId = jwtUtil.extractUserIdFromAccessToken(token);


        CartResponseDTO updatedCart = cartService.decreaseProductQuantity(userId, productId);
        return ResponseEntity.ok(updatedCart);
    }


    @Operation(summary = "Migrate guest cart", description = "Migrates a guest session's cart to the authenticated client's cart.")
    @PostMapping("/cart/migrate")
    public ResponseEntity<Void> migrateGuestCartToClient(
            @RequestParam("guestSessionId") String guestSessionId,
            HttpServletRequest httpRequest
    ) {

        String token = httpRequest.getHeader("Authorization").substring(7);
        String userId = jwtUtil.extractUserIdFromAccessToken(token);


        cartService.migrateGuestCartToClient(userId, guestSessionId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/client/evolution/monthly")
    @PreAuthorize("hasRole('MODERATEUR')")
    public ResponseEntity<Map<String, Long>> getMonthlyClientEvolution() {
        Map<String, Long> monthlyEvolution = clientService.getMonthlyClientEvolution();
        return ResponseEntity.ok(monthlyEvolution);
    }


}
