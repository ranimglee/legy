package user;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import shared.dto.RestaurantDTO;
import user.application.dto.in.*;
import user.application.exception.ZoneNotFoundException;
import user.application.service.DeliveryZoneService;
import user.domain.model.DeliveryZone;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import user.adapters.rest.ZoneController;

import user.application.dto.out.DeliveryZoneResponseDto;
import user.application.dto.out.ZoneResponse;
import user.domain.model.DriverZoneAssignment;


import java.util.ArrayList;

class ZoneControllerTest {

    @InjectMocks
    private ZoneController zoneController;

    @Mock
    private DeliveryZoneService zoneService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateZone() {
        ZoneRequest request = ZoneRequest.builder()
                .zoneName("Zone A")
                .coordinates(List.of(List.of(1.0, 2.0)))
                .color("#FF0000")
                .build();
        DeliveryZone mockedZone = new DeliveryZone();
        mockedZone.setZoneId("zone123");
        mockedZone.setZoneName("Zone A");
        mockedZone.setColor("#FF0000");
        mockedZone.setMaxCapacity(0);
        mockedZone.setCoordinates(request.getCoordinates());

        when(zoneService.createZone(any(DeliveryZone.class))).thenReturn(mockedZone);
        when(zoneService.toZoneResponse(mockedZone)).thenReturn(
                new ZoneResponse("zone123", "Zone A", 0, true, request.getCoordinates(), 0, 0, "#FF0000", new ArrayList<>())
        );

        // When
        ResponseEntity<ZoneResponse> response = zoneController.createZone(request);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Zone A", response.getBody().getZoneName());
    }

    @Test
    void testGetZoneById() {
        String id = "zone123";
        DeliveryZone zone = new DeliveryZone();
        zone.setZoneId(id);
        zone.setZoneName("Test Zone");

        when(zoneService.getZoneById(id)).thenReturn(zone);

        ResponseEntity<DeliveryZone> response = zoneController.getZoneById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Test Zone", response.getBody().getZoneName());
    }

    @Test
    void testUpdateZoneMaxCapacity() {
        String zoneId = "zone123";
        int newCapacity = 15;
        DeliveryZone zone = new DeliveryZone();
        zone.setZoneId(zoneId);
        zone.setMaxCapacity(newCapacity);

        when(zoneService.updateZoneMaxCapacity(zoneId, newCapacity)).thenReturn(zone);
        when(zoneService.toZoneResponse(zone)).thenReturn(
                new ZoneResponse(zoneId, "Z", newCapacity, true, null, 0, 0, "#FFF", new ArrayList<>())
        );

        ResponseEntity<ZoneResponse> response = zoneController.updateZoneMaxCapacity(zoneId, newCapacity);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(newCapacity, response.getBody().getMaxCapacity());
    }

    @Test
    void testAssignRestaurantToZone_success() {
        RestaurantAssignmentRequest request = new RestaurantAssignmentRequest("zone123", "resto456");
        DeliveryZoneResponseDto responseDto = new DeliveryZoneResponseDto();

        when(zoneService.assignRestaurantToZone(request)).thenReturn(responseDto);

        ResponseEntity<DeliveryZoneResponseDto> response = zoneController.assignRestaurantToZone(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    void testAssignRestaurantToZone_notFound() {
        RestaurantAssignmentRequest request = new RestaurantAssignmentRequest("invalid", "resto456");

        when(zoneService.assignRestaurantToZone(request)).thenThrow(new ZoneNotFoundException("Zone not found"));

        ResponseEntity<DeliveryZoneResponseDto> response = zoneController.assignRestaurantToZone(request);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testDeleteZone() {
        String zoneId = "zone123";
        doNothing().when(zoneService).deleteZone(zoneId);

        ResponseEntity<Void> response = zoneController.deleteZone(zoneId);

        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testUpdateZone_success() {
        String zoneId = "zone123";
        ZoneRequest request = ZoneRequest.builder()
                .zoneName("Updated Zone")
                .coordinates(List.of(List.of(1.0, 2.0)))
                .color("#00FF00")
                .build();

        DeliveryZone updatedZone = new DeliveryZone(); // mock data
        when(zoneService.updateZone(eq(zoneId), any(ZoneRequest.class))).thenReturn(updatedZone);
        when(zoneService.toZoneResponse(updatedZone)).thenReturn(new ZoneResponse());

        ResponseEntity<ZoneResponse> response = zoneController.updateZone(zoneId, request);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testUpdateZoneStatus() {
        String zoneId = "zone123";
        boolean enabled = false;

        DeliveryZone updated = new DeliveryZone();
        when(zoneService.updateZoneStatus(zoneId, enabled)).thenReturn(updated);
        when(zoneService.toZoneResponse(updated)).thenReturn(new ZoneResponse());

        ResponseEntity<ZoneResponse> response = zoneController.updateZoneStatus(zoneId, enabled);
        assertEquals(200, response.getStatusCodeValue());
    }


    @Test
    void testUnassignDriverFromZone_success() {
        String driverId = "driver123";
        String zoneId = "zone123";

        doNothing().when(zoneService).unassignDriverFromZone(driverId, zoneId);

        ResponseEntity<String> response = zoneController.unassignDriverFromZone(driverId, zoneId);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testUnassignDriverFromZone_notFound() {
        String driverId = "driver123";
        String zoneId = "zone123";

        doThrow(new IllegalStateException("Driver not assigned")).when(zoneService).unassignDriverFromZone(driverId, zoneId);

        ResponseEntity<String> response = zoneController.unassignDriverFromZone(driverId, zoneId);
        assertEquals(404, response.getStatusCodeValue());
    }
    @Test
    void testGetDeliveryAssignmentZone_success() {
        String id = "driver123";
        DriverZoneAssignment assignment = new DriverZoneAssignment();
        when(zoneService.getDeliveryAssignmentZone(id)).thenReturn(assignment);

        ResponseEntity<DriverZoneAssignment> response = zoneController.getDeliveryAssignmentZone(id);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetDeliveryAssignmentZone_notFound() {
        when(zoneService.getDeliveryAssignmentZone(anyString())).thenThrow(new ZoneNotFoundException("Not found"));

        ResponseEntity<DriverZoneAssignment> response = zoneController.getDeliveryAssignmentZone("driver123");
        assertEquals(404, response.getStatusCodeValue());
    }
    @Test
    void testRemoveRestaurantFromZone_success() {
        RestaurantAssignmentRequest request = new RestaurantAssignmentRequest("zone123", "resto123");
        DeliveryZoneResponseDto responseDto = new DeliveryZoneResponseDto();

        when(zoneService.removeRestaurantFromZone(request)).thenReturn(responseDto);

        ResponseEntity<DeliveryZoneResponseDto> response = zoneController.removeRestaurantFromZone(request);
        assertEquals(200, response.getStatusCodeValue());
    }
    @Test
    void testGetAllZones() {
        List<DeliveryZone> mockZones = List.of(new DeliveryZone(), new DeliveryZone());

        when(zoneService.getAllZones()).thenReturn(mockZones);
        when(zoneService.toZoneResponse(any())).thenReturn(new ZoneResponse());

        ResponseEntity<List<ZoneResponse>> response = zoneController.getAllZones();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }
    @Test
    void testGetAssignedRestaurants() {
        List<RestaurantDTO> restaurants = List.of(new RestaurantDTO(), new RestaurantDTO());
        when(zoneService.getAssignedRestaurantsByZone("zone123")).thenReturn(restaurants);

        ResponseEntity<List<RestaurantDTO>> response = zoneController.getAssignedRestaurants("zone123");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }




    @Test
    void testGetRestaurantDistributionByZone() {
        List<ZoneDistributionDTO> distribution = List.of(new ZoneDistributionDTO());
        when(zoneService.getRestaurantDistributionByZone()).thenReturn(distribution);

        List<ZoneDistributionDTO> response = zoneController.getRestaurantDistributionByZone();
        assertEquals(1, response.size());
    }

    @Test
    void testGetDriverReallocationSuggestions() {
        List<String> suggestions = List.of("Driver1 => ZoneA", "Driver2 => ZoneB");
        when(zoneService.suggestDriverReallocationsWithDetails()).thenReturn(suggestions);

        ResponseEntity<List<String>> response = zoneController.getDriverReallocationSuggestions();
        assertEquals(2, response.getBody().size());
    }




}