package user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import shared.dto.ProductInfo;
import shared.port.ProductQueryPort;
import user.application.dto.in.AddToGuestCartRequest;
import user.application.dto.out.CartProductDTO;
import user.application.dto.out.CartResponseDTO;
import user.domain.event.CartProductEventDTO;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuestCartService {

    private final RedisTemplate<String, CartProductEventDTO> redisTemplate;
    private final ProductQueryPort productQueryPort;

    private static final Duration CART_TTL = Duration.ofHours(3);

    public CartResponseDTO addToCart(AddToGuestCartRequest request, String guestSessionId) {
        ProductInfo product = productQueryPort.getProductById(request.getProductId());

        // Retrieve the existing cart for the guest session
        CartProductEventDTO cart = redisTemplate.opsForValue().get(guestSessionId);
        List<CartProductEventDTO.CartProduct> products = (cart != null && cart.getProducts() != null)
                ? new ArrayList<>(cart.getProducts())
                : new ArrayList<>();

        // Check if product already exists in the cart
        Optional<CartProductEventDTO.CartProduct> existing = products.stream()
                .filter(p -> p.getProductId().equals(product.getProductId()))
                .findFirst();

        // If product exists, update quantity
        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + request.getQuantity());
        } else {
            // If product doesn't exist, add a new product to the cart
            products.add(new CartProductEventDTO.CartProduct(
                    product.getProductId(),
                    product.getName(),
                    request.getQuantity(),
                    product.getPricePostCom()
            ));
        }

        // Calculate total price
        double total = products.stream().mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();
        CartProductEventDTO updatedCart = new CartProductEventDTO(guestSessionId, products, total);

        // Save updated cart in Redis
        redisTemplate.opsForValue().set(guestSessionId, updatedCart, CART_TTL);

        return toResponse(products, total);
    }


    public CartResponseDTO getCart(String guestSessionId) {
        CartProductEventDTO cart = redisTemplate.opsForValue().get(guestSessionId);
        if (cart == null || cart.getProducts() == null) {
            return new CartResponseDTO(new ArrayList<>(), 0.0);
        }
        return toResponse(cart.getProducts(), cart.getTotalPrice());
    }

    public CartResponseDTO decreaseQuantity(String guestSessionId, String productId) {
        CartProductEventDTO cart = redisTemplate.opsForValue().get(guestSessionId);
        if (cart == null || cart.getProducts() == null) {
            return getCart(guestSessionId);
        }

        List<CartProductEventDTO.CartProduct> products = new ArrayList<>();
        for (CartProductEventDTO.CartProduct p : cart.getProducts()) {
            if (p.getProductId().equals(productId)) {
                if (p.getQuantity() > 1) {
                    p.setQuantity(p.getQuantity() - 1);
                    products.add(p);
                }
            } else {
                products.add(p);
            }
        }

        double total = products.stream().mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();
        redisTemplate.opsForValue().set(guestSessionId, new CartProductEventDTO(guestSessionId, products, total), CART_TTL);

        return toResponse(products, total);
    }

    public CartResponseDTO removeItem(String guestSessionId, String productId) {
        CartProductEventDTO cart = redisTemplate.opsForValue().get(guestSessionId);
        if (cart == null || cart.getProducts() == null) {
            return getCart(guestSessionId);
        }

        List<CartProductEventDTO.CartProduct> products = cart.getProducts().stream()
                .filter(p -> !p.getProductId().equals(productId))
                .toList();

        double total = products.stream().mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();
        redisTemplate.opsForValue().set(guestSessionId, new CartProductEventDTO(guestSessionId, products, total), CART_TTL);

        return toResponse(products, total);
    }

    public void clearCart(String guestSessionId) {
        redisTemplate.delete(guestSessionId);
    }

    private CartResponseDTO toResponse(List<CartProductEventDTO.CartProduct> products, double total) {
        List<CartProductDTO> responseProducts = products.stream()
                .map(p -> new CartProductDTO(p.getProductId(), p.getProductName(), p.getQuantity(), p.getPrice()))
                .toList();
        return new CartResponseDTO(responseProducts, total);
    }
}
