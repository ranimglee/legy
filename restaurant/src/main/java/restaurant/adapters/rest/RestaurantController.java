package restaurant.adapters.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import restaurant.application.dto.Notification.OrderNotification;
import org.springframework.web.server.ResponseStatusException;
import restaurant.application.dto.Restaurant.*;
import restaurant.application.usecase.Restaurant.*;
import restaurant.config.JwtConfig;

import restaurant.domain.model.Restaurant;
import restaurant.domain.repository.RestaurantRepository;
import restaurant.domain.service.RestaurantDomainService;
import restaurant.domain.service.RestaurantReportService;

import restaurant.domain.service.RestaurantStatusService;
import shared.config.security.JwtUtil;

import restaurant.application.dto.Category.CategoryResponseDTO;
import restaurant.application.dto.Product.ProductSummaryDTO;
import restaurant.application.usecase.Category.GetRestaurantCategoriesUseCase;
import restaurant.application.usecase.Product.GetProductsByRestaurantAndCategoryUseCase;
import restaurant.domain.model.InternationalCuisine;
import restaurant.domain.model.MainCuisineType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.http.*;
import shared.dto.PagedResponse;


@RestController
@RequestMapping("/restaurant")
@Slf4j
public class RestaurantController {

    private final AddRestaurantUseCase addRestaurantUseCase;
    private final GetAllRestaurantsUseCase getAllRestaurantsUseCase;
    private final GetRestaurantByIdUseCase getRestaurantByIdUseCase;
    private final UpdateRestaurantUseCase updateRestaurantUseCase;
    private final UploadRestaurantLogoUseCase uploadRestaurantLogoUseCase;

    private final AddAvisUseCase addAvisUseCase;
    private final GetAvisByRestaurantUseCase getAvisUseCase;
    private final JwtUtil jwtUtil;
    private final GetNearbyRestaurantsUseCase nearbyUseCase;
    private final GetRecommendedRestaurantsUseCase recommendedUseCase;
    private final GetPopularRestaurantsUseCase getPopularRestaurantsUseCase;
    private final GetRestaurantCategoriesUseCase getCategories;
    private final GetProductsByRestaurantAndCategoryUseCase getProducts;
    private final RestaurantStatusService restaurantStatusService;
    private final JwtConfig jwtConfig;
    private final RestaurantDomainService restaurantDomainService;
    private final RestaurantReportService restaurantReportService;
    private final ApproveRestaurantUseCase approveRestaurantUseCase;
    private final RefuseRestaurantUseCase refuseRestaurantUseCase;
    private final RestaurantRepository  restaurantRepository;
    @Qualifier("saveRestoNotifRedisTemplate")
    private final RedisTemplate redisTemplate;

    public RestaurantController(AddRestaurantUseCase addRestaurantUseCase, GetAllRestaurantsUseCase getAllRestaurantsUseCase, GetRestaurantByIdUseCase getRestaurantByIdUseCase, UpdateRestaurantUseCase updateRestaurantUseCase, UploadRestaurantLogoUseCase uploadRestaurantLogoUseCase, AddAvisUseCase addAvisUseCase, GetAvisByRestaurantUseCase getAvisUseCase, JwtUtil jwtUtil, GetNearbyRestaurantsUseCase nearbyUseCase, GetRecommendedRestaurantsUseCase recommendedUseCase, GetPopularRestaurantsUseCase getPopularRestaurantsUseCase, GetRestaurantCategoriesUseCase getCategories, GetProductsByRestaurantAndCategoryUseCase getProducts, RestaurantStatusService restaurantStatusService, JwtConfig jwtConfig, RestaurantDomainService restaurantDomainService, RestaurantReportService restaurantReportService, ApproveRestaurantUseCase approveRestaurantUseCase, RefuseRestaurantUseCase refuseRestaurantUseCase, RestaurantRepository restaurantRepository, @Qualifier("saveRestoNotifRedisTemplate")
     RedisTemplate redisTemplate) {
        this.addRestaurantUseCase = addRestaurantUseCase;
        this.getAllRestaurantsUseCase = getAllRestaurantsUseCase;
        this.getRestaurantByIdUseCase = getRestaurantByIdUseCase;
        this.updateRestaurantUseCase = updateRestaurantUseCase;
        this.uploadRestaurantLogoUseCase = uploadRestaurantLogoUseCase;
        this.addAvisUseCase = addAvisUseCase;
        this.getAvisUseCase = getAvisUseCase;
        this.jwtUtil = jwtUtil;
        this.nearbyUseCase = nearbyUseCase;
        this.recommendedUseCase = recommendedUseCase;
        this.getPopularRestaurantsUseCase = getPopularRestaurantsUseCase;
        this.getCategories = getCategories;
        this.getProducts = getProducts;
        this.restaurantStatusService = restaurantStatusService;
        this.jwtConfig = jwtConfig;
        this.restaurantDomainService = restaurantDomainService;
        this.restaurantReportService = restaurantReportService;
        this.approveRestaurantUseCase = approveRestaurantUseCase;
        this.refuseRestaurantUseCase = refuseRestaurantUseCase;
        this.restaurantRepository = restaurantRepository;
        this.redisTemplate = redisTemplate;
    }

    @PreAuthorize("hasRole('MODERATEUR')")
    @PatchMapping("/{id-restaurant}/approve-restaurant")
    public ResponseEntity<Restaurant> approveRestaurant(@RequestParam String id) {
        Restaurant approved = approveRestaurantUseCase.execute(id);
        return ResponseEntity.ok(approved);
    }
    @PreAuthorize("hasRole('MODERATEUR')")
    @PatchMapping("/{id-restaurant}/refuse-restaurant")
    public ResponseEntity<Restaurant> refuseRestaurant(@RequestParam String id) {
        Restaurant refused = refuseRestaurantUseCase.execute(id);
        return ResponseEntity.ok(refused);
    }


    @PostMapping("/add-restaurant")
    public RestaurantResponseDTO addRestaurant(@Valid @RequestBody RestaurantRequestDTO request,
                                               HttpServletRequest httpRequest) {

        String authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String userId = jwtConfig.extractUserIdFromAccessToken(token);
            request.setCreatedby(userId);
            System.out.println("Extracted UserId from JWT: " + userId);
        } else {
            throw new RuntimeException("JWT token missing or invalid.");
        }
        return addRestaurantUseCase.execute(request);
    }

    @GetMapping("/get-all-restaurants")
    public List<RestaurantResponseDTO> getAllRestaurants() {
        return getAllRestaurantsUseCase.execute();
    }

    @GetMapping("/get-restaurant-by-id/{id}")
    public RestaurantResponseDTO getRestaurantById(@PathVariable String id) {
        return getRestaurantByIdUseCase.execute(id);
    }

    @PatchMapping("/update-restaurant/{managerId}")
    public RestaurantResponseDTO updateRestaurant(@PathVariable String managerId,
                                                  @Valid @RequestBody RestaurantRequestDTO request,
                                                  HttpServletRequest httpRequest) {

        String authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String userId = jwtConfig.extractUserIdFromAccessToken(token);
            request.setCreatedby(userId);
            System.out.println("Extracted UserId from JWT: " + userId);
        } else {
            throw new RuntimeException("JWT token missing or invalid.");
        }
        return updateRestaurantUseCase.execute(managerId, request);
    }


    @PostMapping(value = "/upload-restaurant-logo/{id}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadRestaurantLogo(@PathVariable String id,
                                       @RequestParam("logo") MultipartFile logo) throws IOException {
        return uploadRestaurantLogoUseCase.execute(id, logo);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<String> getRestaurantStatus(@PathVariable String id) {
        Restaurant restaurant = restaurantDomainService.getRestaurantById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        boolean isOpen = restaurantStatusService.isOpenNow(restaurant);
        return ResponseEntity.ok(isOpen ? "OPEN" : "CLOSED");
    }



    @PostMapping("/{id}/avis")
    public AvisResponseDTO addOrUpdateAvis(
            @PathVariable("id") String restaurantId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @Valid @RequestBody AddAvisRequestDTO request
    ) {
        String token = authHeader.replaceFirst("^Bearer\\s+", "");
        String userId = jwtUtil.extractUserIdFromAccessToken(token);
        return addAvisUseCase.execute(restaurantId, userId, request);
    }

    @GetMapping("/get-all-reviews/{id}")
    public ResponseEntity<PagedResponse<AvisResponseDTO>> listAvisPaged(
            @PathVariable("id") String restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<AvisResponseDTO> pagedAvis = getAvisUseCase.executePaged(restaurantId, pageable);

        PagedResponse<AvisResponseDTO> response = new PagedResponse<>(
                pagedAvis.getContent(),
                pagedAvis.getNumber(),
                pagedAvis.getTotalPages(),
                pagedAvis.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }



    @GetMapping("/get-nearby-restaurants")
    public List<RestaurantResponseDTO> nearby(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam(defaultValue = "5") double maxDistanceKm,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return nearbyUseCase.execute(
                longitude, latitude, maxDistanceKm, limit
        );
    }

    @GetMapping("/top-rated")
    public List<RestaurantResponseDTO> recommended(
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "limit must be at least 1")
            int limit
    ) {
        return recommendedUseCase.execute(limit);
    }



    /**
     * Update only the commission of a restaurant.
     *
     * @param restaurantId The ID of the restaurant.
     * @param newCommission The new commission value.
     * @return The updated restaurant.
     */
    @PreAuthorize("hasRole('MODERATEUR')")
    @PatchMapping("/update-restaurant-commission/{restaurantId}")
    public ResponseEntity<Restaurant> updateRestaurantCommission(
            @PathVariable String restaurantId,
            @RequestParam double newCommission) {
        Restaurant updatedRestaurant = restaurantDomainService.updateRestaurantCommission(restaurantId, newCommission);
        return ResponseEntity.ok(updatedRestaurant);
    }



    @GetMapping("/export/pdf")
    public ResponseEntity<Resource> exportToPdf(
    ) throws  JRException {
        byte[] data = restaurantReportService.generateRestaurantsReport();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=restaurants.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new ByteArrayResource(data));
    }
    @PreAuthorize("hasRole('FINANCIER')")
    @GetMapping("/get-all-restaurants-summarize")
    public ResponseEntity<List<RestaurantSummaryDto>> getRestaurantSummaries() {
        List<RestaurantSummaryDto> summaries = restaurantDomainService.getAllRestaurantsSummary();
        return ResponseEntity.ok(summaries);
    }

    @PreAuthorize("hasRole('FINANCIER')")
    @GetMapping("/average-commission-rate")
    public double getAverageCommissionRate() {
        return restaurantDomainService.getAverageCommissionRate();
    }

    @GetMapping("/popular")
    public PagedResponseDTO<RestaurantSummary> popularByCuisine(
            @RequestParam MainCuisineType main,
            @RequestParam(required = false) InternationalCuisine sub,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return getPopularRestaurantsUseCase.execute(main, sub, page, size);
    }

    @GetMapping("/{id}/categories")
    public List<CategoryResponseDTO> categories(
            @PathVariable("id") String restaurantId
    ) {
        return getCategories.execute(restaurantId);
    }

    @GetMapping("/{id}/products")
    public List<ProductSummaryDTO> productsByCategory(
            @PathVariable("id") String restaurantId,
            @RequestParam("categoryId") String categoryId
    ) {
        return getProducts.execute(restaurantId, categoryId);
    }


    @GetMapping("notification/{restaurantId}")
    public List<OrderNotification> getNotifications(@PathVariable String restaurantId) {
        Set<String> keys = redisTemplate.keys("restaurant:notif:" + restaurantId + ":*");
        if (keys == null || keys.isEmpty()) return Collections.emptyList();

        List<OrderNotification> notifications = new ArrayList<>();
        for (String key : keys) {
            Object notif = redisTemplate.opsForValue().get(key);
            if (notif instanceof OrderNotification) {
                notifications.add((OrderNotification) notif);
            }
        }
        return notifications;
    }
    @GetMapping("/internal/api/snapshot/{id}")
    public ResponseEntity<RestaurantSnapshotDto> getSnapshot(@PathVariable String id) {
        log.info("📡 Fetching snapshot for restaurant [{}]", id);

        Restaurant r = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found: " + id));

        RestaurantSnapshotDto snapshot = new RestaurantSnapshotDto(
                r.getId(),
                r.getAssignedZoneId(),
                r.getNom(),
                r.getAdresse(),
                r.getEmail(),
                r.getTelephone(),
                r.getLongitude(),
                r.getLatitude(),
                r.getIsAssigned()
        );

        log.info("✅ Returning snapshot for restaurant [{}]", id);
        return ResponseEntity.ok(snapshot);
    }

    @GetMapping("/get-all-non-assigned-restaurants")
    public ResponseEntity<List<RestaurantResponseDTO>> getUnassignedRestaurants() {
        List<Restaurant> unassignedRestaurants = restaurantRepository.findByIsAssignedFalseOrIsAssignedNull();

        // Map entities to DTOs (adjust this depending on your existing mapper or use case)
        List<RestaurantResponseDTO> dtos = unassignedRestaurants.stream()
                .map(r -> new RestaurantResponseDTO(
                        r.getId(),
                        r.getNom(),
                        r.getAdresse(),
                        r.getEmail(),
                        r.getTelephone(),
                        r.getLongitude(),
                        r.getLatitude(),
                        r.getIsAssigned()
                ))
                .toList();

        return ResponseEntity.ok(dtos);
    }
}
