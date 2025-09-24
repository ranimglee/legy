package user.adapters.rest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import user.application.service.LivreurTrackingService;
import user.domain.value.LivreurLocationDTO;

@RestController
@RequestMapping("/api/v1/livreurs")
@Tag(name = "Livreur Tracking", description = "Endpoints for tracking livreur real-time location")

public class LivreurTrackingController {

    private final LivreurTrackingService trackingService;

    public LivreurTrackingController(LivreurTrackingService trackingService) {
        this.trackingService = trackingService;
    }

    /**
     * GET /api/v1/livreurs/{livreurId}/position
     *
     * @return 200 OK + DTO si position trouvée, 204 No Content sinon
     */
    @Operation(
            summary = "Get current position of a livreur",
            description = "Returns the latest known GPS coordinates of a livreur. Returns 204 if no location data is found."
    )
    @GetMapping("/{livreurId}/position")
    public ResponseEntity<LivreurLocationDTO> getPosition(@PathVariable String livreurId) {
        return trackingService.getLocation(livreurId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
