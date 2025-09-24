package restaurant.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import restaurant.application.dto.SearchCriteria;
import restaurant.application.dto.SearchResultDTO;
import restaurant.domain.model.MainCuisineType;
import restaurant.infrastructure.service.SearchService;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Search for restaurants by name, cuisine type, rating, category, or price range")
public class SearchController {

    private final SearchService searchService;

    @Operation(
            summary = "Search restaurants",
            description = "Performs a search on restaurants using optional filters such as keyword, cuisine type, rating, category, and price range."
    )
    @GetMapping
    public ResponseEntity<List<SearchResultDTO>> search(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "cuisineType", required = false) MainCuisineType cuisineType,
            @RequestParam(value = "categoryId", required = false) String categoryId,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice
    ) {
        // trim and null-check
        String q = (query == null ? null : query.trim());
        SearchCriteria criteria = new SearchCriteria(q, cuisineType, categoryId, minRating, minPrice, maxPrice);
        List<SearchResultDTO> results = searchService.search(criteria);
        return ResponseEntity.ok(results);
    }
}
