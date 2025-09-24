package restaurant.adapters.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurant.application.dto.Product.GuestProductResponseDTO;
import restaurant.application.repository.ListGuestProductsUseCase;
import restaurant.application.usecase.Product.GetGuestProductByIdUseCase;
import restaurant.domain.model.Product;
import restaurant.domain.model.ProductFilterCriteria;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/products/guest")
public class GuestProductController {

    private final ListGuestProductsUseCase listGuestProductsUseCase;
    private final GetGuestProductByIdUseCase getGuestProductByIdUseCase;

    @Autowired
    public GuestProductController(ListGuestProductsUseCase listGuestProductsUseCase, GetGuestProductByIdUseCase getGuestProductByIdUseCase) {
        this.listGuestProductsUseCase = listGuestProductsUseCase;
        this.getGuestProductByIdUseCase = getGuestProductByIdUseCase;
    }

    @GetMapping()
    public Page<Product> listGuestProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortBy
    ) {
        ProductFilterCriteria criteria = ProductFilterCriteria.builder()
                .categoryId(Optional.ofNullable(category))
                .minPrice(Optional.ofNullable(minPrice))
                .maxPrice(Optional.ofNullable(maxPrice))
                .keyword(Optional.ofNullable(keyword))
                .sortBy(Optional.ofNullable(sortBy))
                .page(page)
                .size(size)
                .build();

        return listGuestProductsUseCase.handle(criteria);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuestProductResponseDTO> getProductByIdForGuest(@PathVariable String id) {
        GuestProductResponseDTO dto = getGuestProductByIdUseCase.execute(id);
        return ResponseEntity.ok(dto);
    }
}
