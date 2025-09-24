package user.adapters.rest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shared.dto.RestaurantDTO;
import user.application.dto.in.*;
import user.application.dto.out.DeliveryZoneResponseDto;
import user.application.dto.out.ZoneResponse;
import user.application.exception.ZoneNotFoundException;
import user.application.service.DeliveryZoneService;
import user.domain.model.DeliveryZone;
import user.domain.model.DriverZoneAssignment;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/moderateur/zones")
@AllArgsConstructor
@Slf4j
public class ZoneController {

    private final DeliveryZoneService zoneService;

    @PostMapping("/create-zone")
    public ResponseEntity<ZoneResponse> createZone(@RequestBody ZoneRequest request) {
        DeliveryZone zone = new DeliveryZone();
        zone.setZoneName(request.getZoneName());
        zone.setMaxCapacity(0);

        zone.setCoordinates(request.getCoordinates());
        zone.setColor(request.getColor());
        DeliveryZone createdZone = zoneService.createZone(zone);

        ZoneResponse response = new ZoneResponse(
                createdZone.getZoneId(),
                createdZone.getZoneName(),
                createdZone.getMaxCapacity(),
                createdZone.isEnabled(),
                createdZone.getCoordinates(),
                createdZone.getNbrAssignedDrivers(),
                createdZone.getNbrRestaurants(),
                createdZone.getColor(),
                createdZone.getAssignedRestaurants()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-zone/{id}")
    public ResponseEntity<ZoneResponse> updateZone(
            @PathVariable String id,
            @Valid @RequestBody ZoneRequest request) {

        // Call service to update the zone
        DeliveryZone updatedZone = zoneService.updateZone(id, request);

        // Map the updated zone to a ZoneResponse
        ZoneResponse response = zoneService.toZoneResponse(updatedZone);

        // Return response with HTTP 200 OK status
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete/{zoneId}")
    public ResponseEntity<Void> deleteZone(@PathVariable String zoneId) {
        zoneService.deleteZone(zoneId);
        return ResponseEntity.noContent().build();
    }


    // Assign a driver to a zone
    @PostMapping("/deliverers/{delivererId}/assign-zone")
    public ResponseEntity<String> assignZoneToDriver(
            @PathVariable String delivererId,
            @RequestParam String zoneId) {
        zoneService.assignDriverToZone(delivererId, zoneId);
        return ResponseEntity.ok("Driver successfully assigned to the zone");
    }

    @PatchMapping("/moderateur/zones/deliverers/{livreurId}/unassign-zone")
    public ResponseEntity<String> unassignDriverFromZone(@PathVariable String livreurId, @RequestParam String zoneId) {
        try {
            zoneService.unassignDriverFromZone(livreurId,zoneId);
            return ResponseEntity.ok("✅ Driver successfully unassigned from zone.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("⚠️ " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Error occurred while unassigning driver.");
        }
    }
    @PatchMapping("/zones/{zoneId}/status")
    public ResponseEntity<ZoneResponse> updateZoneStatus(
            @PathVariable String zoneId,
            @RequestParam boolean enabled) {

        DeliveryZone updated = zoneService.updateZoneStatus(zoneId, enabled);
        ZoneResponse response = zoneService.toZoneResponse(updated);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-zone-by-id/{id}")
    public ResponseEntity<DeliveryZone> getZoneById(@PathVariable String id) {
        DeliveryZone zoneResponse = zoneService.getZoneById(id);
        return ResponseEntity.ok(zoneResponse);
    }

    @GetMapping("/get-delivery-assignment-zone-by-id/{id}")
    public ResponseEntity<DriverZoneAssignment> getDeliveryAssignmentZone(@PathVariable String id) {
        try {
            DriverZoneAssignment assignment = zoneService.getDeliveryAssignmentZone(id);
            return ResponseEntity.ok(assignment);
        } catch (ZoneNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Get all zones
    @GetMapping("/get-zones")
    public ResponseEntity<List<ZoneResponse>> getAllZones() {
        List<DeliveryZone> zones = zoneService.getAllZones();
        List<ZoneResponse> response = zones.stream()
                .map(zoneService::toZoneResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

   @PostMapping("/restaurants/assign")
    public ResponseEntity<DeliveryZoneResponseDto> assignRestaurantToZone(@RequestBody RestaurantAssignmentRequest request) {
        try {
            DeliveryZoneResponseDto updatedZone = zoneService.assignRestaurantToZone(request);
            return ResponseEntity.ok(updatedZone);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (ZoneNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/remove-restaurant")
    public ResponseEntity<DeliveryZoneResponseDto> removeRestaurantFromZone(@RequestBody RestaurantAssignmentRequest request) {
        try {
            DeliveryZoneResponseDto updatedZone = zoneService.removeRestaurantFromZone(request);
            return ResponseEntity.ok(updatedZone);
        } catch (ZoneNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{zoneId}/update-max-capacity")
    public ResponseEntity<ZoneResponse> updateZoneMaxCapacity(
            @PathVariable String zoneId,
            @RequestParam int newMaxCapacity) {

        DeliveryZone updatedZone = zoneService.updateZoneMaxCapacity(zoneId, newMaxCapacity);
        ZoneResponse response = zoneService.toZoneResponse(updatedZone);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/zones/driver-reallocation-suggestions")
    public ResponseEntity<List<String>> getDriverReallocationSuggestions() {
        List<String> suggestions = zoneService.suggestDriverReallocationsWithDetails();
        return ResponseEntity.ok(suggestions);
    }
    // Get assigned restaurants in a specific zone
    @GetMapping("/{zoneId}/restaurants")
    public ResponseEntity<List<RestaurantDTO>> getAssignedRestaurants(@PathVariable String zoneId) {
        List<RestaurantDTO> restaurants = zoneService.getAssignedRestaurantsByZone(zoneId);
        return ResponseEntity.ok(restaurants);
    }

    // Get assigned drivers in a specific zone
    @GetMapping("/{zoneId}/drivers")
    public ResponseEntity<List<LivreurDTO>> getAssignedDrivers(@PathVariable String zoneId) {
        List<LivreurDTO> drivers = zoneService.getAssignedDriversWithDetails(zoneId);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/{zoneId}/available-non-assigned-restaurants")
    public ResponseEntity<List<RestaurantDTO>> getAvailableNonAssignedRestaurants(@PathVariable String zoneId) {
        log.info("🔍 Fetching available non-assigned restaurants in zone [{}]", zoneId);
        List<RestaurantDTO> availableRestaurants = zoneService.getAvailableNonAssignedRestaurantsInZone(zoneId);
        return ResponseEntity.ok(availableRestaurants);
    }

    @GetMapping("/restaurants/distribution-by-zone")
    public List<ZoneDistributionDTO> getRestaurantDistributionByZone() {
        return zoneService.getRestaurantDistributionByZone();
    }

    @GetMapping("/get-all-unassigned-drivers")
    public ResponseEntity<List<LivreurDTO>> getAllUnassignedLivreurs() {
        List<LivreurDTO> livreurs = zoneService.getAllUnassignedLivreurs();
        return ResponseEntity.ok(livreurs);
    }
    @GetMapping("/available-capacity-zones")
    public List<DeliveryZoneSummaryDTO> getZonesWithAvailableCapacity() {
        log.info("📥 API call: Get zones with available driver capacity");
        return zoneService.getZonesWithAvailableCapacity();
    }

    @PreAuthorize("hasRole('FINANCIER')")
    @GetMapping("/get-all-zones")
    public List<DeliveryZoneSummaryDTO> getZonesForPayslip() {
        return zoneService.getZones();
    }
}
