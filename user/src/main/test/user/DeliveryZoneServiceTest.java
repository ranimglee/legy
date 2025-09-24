package user;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;

import shared.domain.event.RestaurantAssignedToZoneEvent;

import user.adapters.rest.RestaurantClient;
import user.application.dto.in.*;
import user.application.dto.out.ZoneResponse;
import user.application.exception.ZoneNotFoundException;
import user.application.service.DeliveryZoneService;

import user.domain.model.DeliveryZone;
import user.domain.model.DriverZoneAssignment;
import user.domain.model.LivreurEntity;
import user.domain.repository.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeliveryZoneServiceTest {

    @InjectMocks
    private DeliveryZoneService deliveryZoneService;

    @Mock private DeliveryZoneRepository zoneRepo;
    @Mock private DriverZoneAssignmentRepository driverRepo;

    @Mock private RestaurantClient restaurantClient;

    @Mock private UserRepository userRepository;
    @Mock
    private KafkaTemplate<String, RestaurantAssignedToZoneEvent> restaurantAssignedKafkaTemplate;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateZone_ShouldEnableAndSave() {
        DeliveryZone zone = new DeliveryZone();
        zone.setEnabled(false);

        when(zoneRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        DeliveryZone result = deliveryZoneService.createZone(zone);

        assertTrue(result.isEnabled());
        verify(zoneRepo).save(zone);
    }

    @Test
    public void testGetZoneById_Success() {
        DeliveryZone zone = new DeliveryZone();
        zone.setZoneId("z123");
        when(zoneRepo.findById("z123")).thenReturn(Optional.of(zone));

        DeliveryZone result = deliveryZoneService.getZoneById("z123");

        assertEquals("z123", result.getZoneId());
    }

    @Test
    public void testGetZoneById_NotFound() {
        when(zoneRepo.findById("invalid")).thenReturn(Optional.empty());
        assertThrows(ZoneNotFoundException.class, () -> deliveryZoneService.getZoneById("invalid"));
    }

    @Test
    public void testAssignDriverToZone_Success() {
        String zoneId = "zone123", driverId = "driver1";
        DeliveryZone zone = new DeliveryZone();
        zone.setZoneId(zoneId);
        zone.setMaxCapacity(2);
        zone.setNbrAssignedDrivers(0);

        when(zoneRepo.findById(zoneId)).thenReturn(Optional.of(zone));
        when(driverRepo.countByZoneIdAndAssignedTrue(zoneId)).thenReturn(0L);
        when(driverRepo.findByLivreurIdAndZoneId(driverId, zoneId)).thenReturn(Optional.empty());
        when(driverRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<DriverZoneAssignment> result = deliveryZoneService.assignDriverToZone(driverId, zoneId);
        assertTrue(result.isPresent());
        verify(zoneRepo).save(zone);
    }

    @Test
    public void testAssignDriverToZone_MaxCapacityReached() {
        DeliveryZone zone = new DeliveryZone();
        zone.setZoneId("z1");
        zone.setMaxCapacity(1);
        when(zoneRepo.findById("z1")).thenReturn(Optional.of(zone));
        when(driverRepo.countByZoneIdAndAssignedTrue("z1")).thenReturn(1L);

        assertThrows(IllegalStateException.class, () ->
                deliveryZoneService.assignDriverToZone("driver1", "z1")
        );
    }

    @Test
    public void testUnassignDriverFromZone_Success() {
        DriverZoneAssignment assignment = new DriverZoneAssignment();
        assignment.setAssigned(true);
        when(driverRepo.findByLivreurIdAndZoneId("d1", "z1")).thenReturn(Optional.of(assignment));
        DeliveryZone zone = new DeliveryZone();
        zone.setZoneId("z1");
        when(zoneRepo.findById("z1")).thenReturn(Optional.of(zone));
        when(driverRepo.countByZoneIdAndAssignedTrue("z1")).thenReturn(0L);

        deliveryZoneService.unassignDriverFromZone("d1", "z1");

        verify(driverRepo).save(assignment);
        assertFalse(assignment.isAssigned());
    }

    @Test
    public void testUpdateZoneStatus() {
        DeliveryZone zone = new DeliveryZone();
        zone.setEnabled(false);
        when(zoneRepo.findById("z1")).thenReturn(Optional.of(zone));
        when(zoneRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        DeliveryZone updated = deliveryZoneService.updateZoneStatus("z1", true);
        assertTrue(updated.isEnabled());
    }

    @Test
    public void testUpdateZoneMaxCapacity() {
        DeliveryZone zone = new DeliveryZone();
        zone.setMaxCapacity(2);
        when(zoneRepo.findById("z1")).thenReturn(Optional.of(zone));
        when(zoneRepo.save(any())).thenReturn(zone);

        DeliveryZone updated = deliveryZoneService.updateZoneMaxCapacity("z1", 10);
        assertEquals(10, updated.getMaxCapacity());
    }



    @Test
    public void testRemoveRestaurantFromZone_NotFound() {
        when(zoneRepo.findById("z1")).thenReturn(Optional.empty());
        RestaurantAssignmentRequest req = new RestaurantAssignmentRequest("z1", "r1");
        assertThrows(ZoneNotFoundException.class, () -> deliveryZoneService.removeRestaurantFromZone(req));
    }

    @Test
    public void testGetAllUnassignedLivreurs() {
        LivreurEntity l = new LivreurEntity();
        l.setId("id1");
        l.setEmail("test@test.com");
        when(userRepository.findByIsAssignedFalse()).thenReturn(List.of(l));
        List<LivreurDTO> result = deliveryZoneService.getAllUnassignedLivreurs();
        assertEquals(1, result.size());
    }

    @Test
    public void testToZoneResponse() {
        DeliveryZone zone = new DeliveryZone();
        zone.setZoneId("z1");
        zone.setZoneName("My Zone");
        zone.setEnabled(true);
        zone.setMaxCapacity(10);
        zone.setNbrAssignedDrivers(3);
        zone.setNbrRestaurants(2);
        zone.setColor("#FF0000");

        ZoneResponse response = deliveryZoneService.toZoneResponse(zone);
        assertEquals("z1", response.getZoneId());
        assertEquals("My Zone", response.getZoneName());
        assertEquals(10, response.getMaxCapacity());
    }
}