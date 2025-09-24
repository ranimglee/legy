package user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import shared.dto.ProductInfo;
import shared.port.ProductQueryPort;
import user.application.dto.in.AddToCartRequest;
import user.application.dto.out.CartProductDTO;
import user.application.dto.out.CartResponseDTO;
import user.domain.event.CartProductEventDTO;
import user.infrastructure.kafka.CartEventConsumer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartEventConsumer cartEventConsumer;
    private final RedisTemplate<String, CartProductEventDTO> redisTemplate;
    private final ProductQueryPort productQueryPort;

    public CartProductEventDTO getGuestCart(String guestSessionId) {
        CartProductEventDTO cart = cartEventConsumer.consumeCartFromKafka(guestSessionId);
        return cart != null ? cart : new CartProductEventDTO(guestSessionId, null, 0.0);
    }

    public CartResponseDTO getClientCart(String userId) {
        CartProductEventDTO cart = redisTemplate.opsForValue().get(userId);

        if (cart == null || cart.getProducts() == null) {
            return new CartResponseDTO(new ArrayList<>(), 0.0);
        }

        List<CartProductDTO> productDTOs = cart.getProducts().stream()
                .map(p -> new CartProductDTO(
                        p.getProductId(),
                        p.getProductName(),
                        p.getQuantity(),
                        p.getPrice()
                ))
                .toList();

        return new CartResponseDTO(productDTOs, cart.getTotalPrice());
    }

    public void clearClientCart(String userId) {
        redisTemplate.delete(userId);
    }

    public CartResponseDTO removeProductFromClientCart(String userId, String productId) {
        CartProductEventDTO existingCart = redisTemplate.opsForValue().get(userId);

        if (existingCart == null || existingCart.getProducts() == null) {
            throw new RuntimeException("Cart is empty or not found");
        }

        List<CartProductEventDTO.CartProduct> updatedProducts = existingCart.getProducts().stream()
                .filter(p -> !p.getProductId().equals(productId)) // remove target product
                .toList();

        double newTotal = updatedProducts.stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();

        CartProductEventDTO updatedCart = new CartProductEventDTO(userId, updatedProducts, newTotal);

        redisTemplate.opsForValue().set(userId, updatedCart, Duration.ofHours(3));

        List<CartProductDTO> responseProducts = updatedProducts.stream()
                .map(p -> new CartProductDTO(
                        p.getProductId(),
                        p.getProductName(),
                        p.getQuantity(),
                        p.getPrice()
                )).toList();

        return new CartResponseDTO(responseProducts, newTotal);
    }

    public CartResponseDTO decreaseProductQuantity(String userId, String productId) {
        CartProductEventDTO existingCart = redisTemplate.opsForValue().get(userId);

        if (existingCart == null || existingCart.getProducts() == null) {
            throw new RuntimeException("Cart is empty or not found");
        }

        List<CartProductEventDTO.CartProduct> updatedProducts = new ArrayList<>();

        for (CartProductEventDTO.CartProduct product : existingCart.getProducts()) {
            if (product.getProductId().equals(productId)) {
                if (product.getQuantity() > 1) {
                    product.setQuantity(product.getQuantity() - 1);
                    updatedProducts.add(product);
                }
                // else: don't add it to updated list (means remove it if quantity == 1)
            } else {
                updatedProducts.add(product);
            }
        }

        double newTotal = updatedProducts.stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();

        CartProductEventDTO updatedCart = new CartProductEventDTO(userId, updatedProducts, newTotal);
        redisTemplate.opsForValue().set(userId, updatedCart, Duration.ofHours(3));

        List<CartProductDTO> responseProducts = updatedProducts.stream()
                .map(p -> new CartProductDTO(
                        p.getProductId(),
                        p.getProductName(),
                        p.getQuantity(),
                        p.getPrice()
                ))
                .toList();

        return new CartResponseDTO(responseProducts, newTotal);
    }

    public void migrateGuestCartToClient(String userId, String guestSessionId) {
        CartProductEventDTO guestCart = getGuestCart(guestSessionId);
        if (guestCart != null && guestCart.getProducts() != null) {
            guestCart.getProducts().forEach(product -> addToClientCart(userId, new AddToCartRequest(product.getProductId(), product.getQuantity())));
        }
        clearGuestCart(guestSessionId);
    }

    public CartResponseDTO addToClientCart(String userId, AddToCartRequest request) {
        ProductInfo product = productQueryPort.getProductById(request.getProductId());

        CartProductEventDTO existingCart = redisTemplate.opsForValue().get(userId);
        List<CartProductEventDTO.CartProduct> products = new ArrayList<>();
        if (existingCart != null && existingCart.getProducts() != null) {
            products.addAll(existingCart.getProducts());
        }

        Optional<CartProductEventDTO.CartProduct> existingProduct = products.stream()
                .filter(p -> p.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingProduct.isPresent()) {
            existingProduct.get().setQuantity(existingProduct.get().getQuantity() + request.getQuantity());
        } else {
            products.add(new CartProductEventDTO.CartProduct(product.getProductId(), product.getName(), request.getQuantity(), product.getPricePostCom()));
        }

        double total = products.stream().mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();

        CartProductEventDTO updatedCart = new CartProductEventDTO(userId, products, total);
        redisTemplate.opsForValue().set(userId, updatedCart, Duration.ofHours(3));

        List<CartProductDTO> responseProducts = products.stream()
                .map(p -> new CartProductDTO(p.getProductId(), p.getProductName(), p.getQuantity(), p.getPrice()))
                .toList();

        return new CartResponseDTO(responseProducts, total);
    }

    public void clearGuestCart(String guestSessionId) {
        redisTemplate.delete(guestSessionId);
    }

}
