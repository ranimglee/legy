package user.adapters.rest;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import user.application.dto.in.AddToGuestCartRequest;
import user.application.dto.out.CartResponseDTO;
import user.application.service.GuestCartService;

import java.util.UUID;

@RestController
@RequestMapping("/api/guest/cart")
@RequiredArgsConstructor
public class GuestCartController {

    private final GuestCartService guestCartService;

    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> add(
            @RequestBody AddToGuestCartRequest request,
            HttpSession session
    ) {
        String guestSessionId = (String) session.getAttribute("guestSessionId");

        if (guestSessionId == null) {
            guestSessionId = generateNewGuestSessionId(session);
        }
        System.out.println("Guest Session ID: " + guestSessionId);

        return ResponseEntity.ok(guestCartService.addToCart(request, guestSessionId));
    }


    private String generateNewGuestSessionId(HttpSession session) {
        String newGuestSessionId = UUID.randomUUID().toString();
        session.setAttribute("guestSessionId", newGuestSessionId);
        return newGuestSessionId;
    }

    @GetMapping
    public ResponseEntity<CartResponseDTO> get(@RequestParam String guestSessionId) {
        return ResponseEntity.ok(guestCartService.getCart(guestSessionId));
    }

    @PatchMapping("/decrease/{productId}")
    public ResponseEntity<CartResponseDTO> decrease(
            @PathVariable String productId,
            @RequestParam String guestSessionId
    ) {
        return ResponseEntity.ok(guestCartService.decreaseQuantity(guestSessionId, productId));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartResponseDTO> remove(
            @PathVariable String productId,
            @RequestParam String guestSessionId
    ) {
        return ResponseEntity.ok(guestCartService.removeItem(guestSessionId, productId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clear(@RequestParam String guestSessionId) {
        guestCartService.clearCart(guestSessionId);
        return ResponseEntity.noContent().build();
    }
}
