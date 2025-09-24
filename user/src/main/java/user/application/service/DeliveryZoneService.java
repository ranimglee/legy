package user.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import shared.domain.event.RestaurantRemovedFromZoneEvent;
import shared.dto.RestaurantDTO;
import user.adapters.rest.RestaurantClient;
import user.application.dto.in.*;
import user.application.dto.out.DeliveryZoneResponseDto;
import user.application.dto.out.ZoneResponse;
import user.application.exception.ZoneNotFoundException;
import user.application.utils.GeoUtils;
import shared.domain.event.RestaurantAssignedToZoneEvent;
import user.domain.model.DeliveryZone;
import user.domain.model.DriverZoneAssignment;
import user.domain.model.LivreurEntity;
import user.domain.model.UserEntity;
import user.domain.repository.DeliveryZoneRepository;
import user.domain.repository.DriverZoneAssignmentRepository;
import user.domain.repository.UserRepository;
import user.domain.service.UserService;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j

public class DeliveryZoneService {

    private final DeliveryZoneRepository zoneRepo;
    private final DriverZoneAssignmentRepository driverZoneAssignmentRepository;
    @Qualifier("restaurantRemovedKafkaTemplate")
    private final KafkaTemplate<String, RestaurantRemovedFromZoneEvent> restaurantRemovedKafkaTemplate;

    @Qualifier("restaurantAssignedKafkaTemplate")
    private final KafkaTemplate<String, RestaurantAssignedToZoneEvent> restaurantAssignedKafkaTemplate;

    private final EmailService emailService;
    private final RestaurantClient restaurantClient;
    private final GoogleMapsService googleMapsService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public DeliveryZoneService(
            DeliveryZoneRepository zoneRepo,
            DriverZoneAssignmentRepository driverZoneAssignmentRepository,
            @Qualifier("restaurantAssignedKafkaTemplate")
            KafkaTemplate<String, RestaurantAssignedToZoneEvent> kafkaTemplate,
            @Qualifier("restaurantRemovedKafkaTemplate")
            KafkaTemplate<String, RestaurantRemovedFromZoneEvent> removedKafkaTemplate,

            EmailService emailService,
            RestaurantClient restaurantClient,
            GoogleMapsService googleMapsService,
            UserService userService, UserRepository userRepository) {
        this.zoneRepo = zoneRepo;
        this.driverZoneAssignmentRepository = driverZoneAssignmentRepository;
        this.restaurantAssignedKafkaTemplate = kafkaTemplate;
        this.restaurantRemovedKafkaTemplate=removedKafkaTemplate;
        this.emailService = emailService;
        this.restaurantClient = restaurantClient;
        this.googleMapsService = googleMapsService;
        this.userService = userService;
        this.userRepository = userRepository;
    }



    public DeliveryZone createZone(DeliveryZone zone) {
        if (!zone.isEnabled()) {
            zone.setEnabled(true);  // Set to true if not already set
        }
        return zoneRepo.save(zone);
    }


    public void deleteZone(String zoneId) {
        zoneRepo.deleteById(zoneId);
    }

    public DeliveryZone getZoneById(String zoneId) {
        return zoneRepo.findById(zoneId)
                .orElseThrow(() -> new ZoneNotFoundException("Zone not found"));
    }

    public Optional<DriverZoneAssignment> assignDriverToZone(String livreurId, String zoneId) {
        DeliveryZone zone = zoneRepo.findById(zoneId)
                .orElseThrow(() -> new ZoneNotFoundException("Zone not found"));

        long assignedCount = driverZoneAssignmentRepository.countByZoneIdAndAssignedTrue(zoneId);

        if (assignedCount >= zone.getMaxCapacity()) {
            throw new IllegalStateException("Maximum capacity for this zone has been reached.");
        }

        Optional<DriverZoneAssignment> existingOpt = driverZoneAssignmentRepository.findByLivreurIdAndZoneId(livreurId, zoneId);

        DriverZoneAssignment assignment;

        if (existingOpt.isPresent()) {
            assignment = existingOpt.get();
            if (assignment.isAssigned()) {
                throw new IllegalStateException("Driver is already assigned to this zone.");
            }
            assignment.setAssigned(true);
        } else {
            assignment = new DriverZoneAssignment();
            assignment.setLivreurId(livreurId); // ✅ now a string
            assignment.setZoneId(zoneId);
            assignment.setAssigned(true);
        }

        zone.setNbrAssignedDrivers((int) assignedCount + 1);
        zoneRepo.save(zone);

        return Optional.of(driverZoneAssignmentRepository.save(assignment));
    }

    public void unassignDriverFromZone(String livreurId, String zoneId) {
        DriverZoneAssignment existingAssignment = driverZoneAssignmentRepository
                .findByLivreurIdAndZoneId(livreurId, zoneId)
                .orElseThrow(() -> new IllegalStateException("Driver not assigned to this zone"));

        existingAssignment.setAssigned(false);
        driverZoneAssignmentRepository.save(existingAssignment);

        DeliveryZone zone = zoneRepo.findById(zoneId)
                .orElseThrow(() -> new ZoneNotFoundException("Zone not found"));

        long assignedCount = driverZoneAssignmentRepository.countByZoneIdAndAssignedTrue(zoneId);
        zone.setNbrAssignedDrivers((int) assignedCount);
        zoneRepo.save(zone);
    }

    public DeliveryZone updateZoneStatus(String zoneId, boolean enabled) {
        DeliveryZone zone = zoneRepo.findById(zoneId)
                .orElseThrow(() -> new ZoneNotFoundException("Zone not found"));
        zone.setEnabled(enabled);
        return zoneRepo.save(zone);
    }


    public DeliveryZone updateZone(String id, ZoneRequest request) {
        DeliveryZone zone = zoneRepo.findById(id)
                .orElseThrow(() -> new ZoneNotFoundException("Zone not found"));

        zone.setZoneName(request.getZoneName());
        zone.setCoordinates(request.getCoordinates());

        return zoneRepo.save(zone);
    }
    public ZoneResponse toZoneResponse(DeliveryZone zone) {
        return new ZoneResponse(
                zone.getZoneId(),
                zone.getZoneName(),
                zone.getMaxCapacity(),
                zone.isEnabled(),
                zone.getCoordinates(),
                zone.getNbrAssignedDrivers(),
                zone.getNbrRestaurants(),
                zone.getColor(),
                zone.getAssignedRestaurants()
        );
    }
    public DriverZoneAssignment getDeliveryAssignmentZone(String id) {

        DriverZoneAssignment zone = driverZoneAssignmentRepository.findById(id)
                .orElseThrow(() -> new ZoneNotFoundException("Zone not found for assignment"));

       return zone;
    }
    public List<DeliveryZone> getAllZones() {
        return zoneRepo.findAll();
    }


   public DeliveryZoneResponseDto assignRestaurantToZone(RestaurantAssignmentRequest request) {
        String zoneId = request.getZoneId();
        String restaurantId = request.getRestaurantId();

        log.info("📥 Assigning restaurant [{}] to zone [{}]", restaurantId, zoneId);
        DeliveryZone zone = zoneRepo.findById(zoneId)
                .orElseThrow(() -> new ZoneNotFoundException("Zone not found"));

        if (zoneRepo.findByAssignedRestaurantId(restaurantId).isPresent()) {
            throw new IllegalStateException("Restaurant is already assigned to another zone.");
        }

        RestaurantDTO snapshot = restaurantClient.fetchRestaurantSnapshot(restaurantId);
        if (snapshot == null) {
            throw new IllegalStateException("Failed to fetch restaurant info");
        }

        if (zone.getAssignedRestaurants() == null) {
            zone.setAssignedRestaurants(new ArrayList<>());
        }
        zone.getAssignedRestaurants().add(snapshot);

        int updatedNbrRestaurants = zone.getNbrRestaurants() + 1;
        zone.setNbrRestaurants(updatedNbrRestaurants);

        int driversPerRestaurant = 3;
        zone.setMaxCapacity(updatedNbrRestaurants * driversPerRestaurant);

        DeliveryZone updatedZone = zoneRepo.save(zone);

        RestaurantAssignedToZoneEvent event = new RestaurantAssignedToZoneEvent(
                snapshot.getId(),
                zoneId,
                snapshot.getNom(),
                snapshot.getAdresse(),
                snapshot.getEmail(),
                snapshot.getTelephone(),
                snapshot.getLongitude(),
                snapshot.getLatitude()
        );
       restaurantAssignedKafkaTemplate.send("restaurant.assigned.zone", event);

        log.info("✅ Restaurant [{}] assigned to zone [{}] and event published", restaurantId, zoneId);

        return new DeliveryZoneResponseDto(
                updatedZone.getNbrRestaurants(),
                updatedZone.getNbrAssignedDrivers(),
                updatedZone.getMaxCapacity()
        );
    }

    public DeliveryZoneResponseDto removeRestaurantFromZone(RestaurantAssignmentRequest request) {
        String zoneId = request.getZoneId();
        String restaurantId = request.getRestaurantId();

        log.info("📥 Received request to REMOVE restaurant [{}] from zone [{}]", restaurantId, zoneId);

        DeliveryZone zone = zoneRepo.findById(zoneId)
                .orElseThrow(() -> {
                    log.error("❌ Zone [{}] not found. Aborting restaurant removal.", zoneId);
                    return new ZoneNotFoundException("Zone not found");
                });


        List<RestaurantDTO> assignedRestaurants = zone.getAssignedRestaurants();
        if (assignedRestaurants == null || assignedRestaurants.isEmpty()) {
            log.warn("⚠️ No restaurants are assigned to zone [{}]. Skipping removal.", zoneId);
            return new DeliveryZoneResponseDto(zone.getNbrRestaurants(), zone.getNbrAssignedDrivers(), zone.getMaxCapacity());
        }

        boolean removed = assignedRestaurants.removeIf(r -> r.getId().equals(restaurantId));
        if (!removed) {
            log.warn("⚠️ Restaurant [{}] was not assigned to zone [{}]. No changes made.", restaurantId, zoneId);
            return new DeliveryZoneResponseDto(zone.getNbrRestaurants(), zone.getNbrAssignedDrivers(), zone.getMaxCapacity());
        }

        int updatedNbrRestaurants = zone.getNbrRestaurants() - 1;
        zone.setNbrRestaurants(updatedNbrRestaurants);
        DeliveryZone updatedZone = zoneRepo.save(zone);

        log.info("✅ Restaurant [{}] removed. Zone [{}] now has max capacity [{}]",
                restaurantId, zoneId, updatedZone.getMaxCapacity());

        RestaurantRemovedFromZoneEvent event = new RestaurantRemovedFromZoneEvent(restaurantId, zoneId);
        restaurantRemovedKafkaTemplate.send("restaurant.removed.zone", event);


        log.info("📤 Published removal event to Kafka: {}", event);

        return new DeliveryZoneResponseDto(
                updatedZone.getNbrRestaurants(),
                updatedZone.getNbrAssignedDrivers(),
                updatedZone.getMaxCapacity()
        );
    }




    public DeliveryZone updateZoneMaxCapacity(String zoneId, int newMaxCapacity) {
        DeliveryZone zone = zoneRepo.findById(zoneId)
                .orElseThrow(() -> new ZoneNotFoundException("Zone not found"));

        log.info("✏️ Manually updating maxCapacity for zone [{}] from [{}] to [{}]",
                zoneId, zone.getMaxCapacity(), newMaxCapacity);

        zone.setMaxCapacity(newMaxCapacity);
        return zoneRepo.save(zone);
    }
    @Scheduled(cron = "0 0 8 *  * *") // Daily at midnight
    public void checkZoneCapacityAndLogic() {
        log.info("📅 Running scheduled zone logic check...");

        List<DeliveryZone> allZones = zoneRepo.findAll();
        double unusedThreshold = 0.5;
        int maxDriversPerRestaurant = 4;

        StringBuilder emailContent = new StringBuilder();

        emailContent.append("""
    <html>
    <head>
      <style>
        body {
          font-family: Arial, sans-serif;
          background-color: #f4f4f4;
          padding: 20px;
        }
        .container {
          background-color: #ffffff;
          padding: 20px;
          border-radius: 8px;
          box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        h2 {
          color: #d9534f;
        }
        table {
          width: 100%;
          border-collapse: collapse;
          margin-top: 15px;
        }
        th, td {
          border: 1px solid #ddd;
          padding: 10px;
          text-align: left;
        }
        th {
          background-color: #f8d7da;
          color: #721c24;
        }
        tr:nth-child(even) {
          background-color: #f9f9f9;
        }
        .reason {
          font-weight: bold;
          color: #dc3545;
        }
      </style>
    </head>
    <body>
      <div class="container">
        <h2> Delivery Zone Alert</h2>
        <p>The following zones have been flagged for inconsistent configuration:</p>
        <table>
          <tr>
            <th>Zone Name</th>
            <th>Restaurants</th>
            <th>Drivers Assigned</th>
            <th>Max Capacity</th>
            <th>Issue</th>
          </tr>
""");
        boolean issueDetected = false;

        for (DeliveryZone zone : allZones) {
            int max = zone.getMaxCapacity();
            int assigned = zone.getNbrAssignedDrivers();
            int restaurants = zone.getNbrRestaurants();

            boolean overProvisioned = max > 0 && ((double)(max - assigned) / max) >= unusedThreshold;
            boolean mismatch = restaurants > 0 && assigned > (restaurants * maxDriversPerRestaurant);

            if (overProvisioned || mismatch) {
                issueDetected = true;
                String reason = overProvisioned && mismatch ?
                        "Overprovisioned and too many drivers for restaurant count" :
                        overProvisioned ? "Overprovisioned capacity" :
                                "Too many drivers for restaurant count";

                emailContent.append("<tr>")
                        .append("<td>").append(zone.getZoneName()).append("</td>")
                        .append("<td>").append(restaurants).append("</td>")
                        .append("<td>").append(assigned).append("</td>")
                        .append("<td>").append(max).append("</td>")
                        .append("<td class='reason'>").append(reason).append("</td>")
                        .append("</tr>");



            }

        }
        emailContent.append("""
        </table>
        <p style="margin-top: 20px;">Please review these zones and take appropriate action.</p>
        <a href="https://your-domain.com/dashboard/zones"
           style="display:inline-block;margin-top:10px;padding:10px 15px;background:#28a745;color:white;
           border-radius:5px;text-decoration:none;">View in Dashboard</a>
      </div>
    </body>
    </html>
""");

        if (issueDetected) {
            String subject = "️ Delivery Zone Alert: Inconsistent Configurations Detected";
            String recipient = "legy.services@gmail.com";
            emailService.sendZoneAlertEmail(recipient, subject, emailContent.toString());
            log.info("📧 Alert email sent to {}", recipient);
        } else {
            log.info("✅ No delivery zone inconsistencies detected.");
        }
    }

    public List<String> suggestDriverReallocationsWithDetails() {
        log.info("📊 Starting driver reallocation suggestion process...");

        List<DeliveryZone> allZones = zoneRepo.findAll();
        double MIN_COVERAGE = 1.0;
        double MAX_COVERAGE = 3.0;

        List<String> suggestions = new ArrayList<>();

        List<DeliveryZone> under = new ArrayList<>();
        List<DeliveryZone> over = new ArrayList<>();

        log.info("🔍 Scanning all zones to classify them based on driver-to-restaurant ratio...");

        for (DeliveryZone zone : allZones) {
            if (zone.getNbrRestaurants() == 0) {
                log.info("⚠️ Skipping zone [{}] because it has 0 restaurants", zone.getZoneName());
                continue;
            }

            double driverRatio = (double) zone.getNbrAssignedDrivers() / zone.getNbrRestaurants();
            log.info("📦 Zone [{}] has {} restaurants, {} drivers => ratio: {:.2f}",
                    zone.getZoneName(), zone.getNbrRestaurants(), zone.getNbrAssignedDrivers(), driverRatio);

            if (driverRatio < MIN_COVERAGE) {
                under.add(zone);
                log.info("⬇️ Zone [{}] marked as under-provisioned (needs drivers)", zone.getZoneName());
            } else if (driverRatio > MAX_COVERAGE) {
                over.add(zone);
                log.info("⬆️ Zone [{}] marked as over-provisioned (has extra drivers)", zone.getZoneName());
            }
        }

        for (DeliveryZone overZone : over) {
            int excessDrivers = overZone.getNbrAssignedDrivers() - (int)(MAX_COVERAGE * overZone.getNbrRestaurants());
            log.info("🔁 Processing over-provisioned zone [{}] with {} excess driver(s)...",
                    overZone.getZoneName(), excessDrivers);

            List<DriverZoneAssignment> overDrivers = new ArrayList<>(
                    driverZoneAssignmentRepository.findByZoneIdAndAssignedTrue(overZone.getZoneId())
            );

            for (DeliveryZone underZone : under) {
                int needed = (int)(MIN_COVERAGE * underZone.getNbrRestaurants()) - underZone.getNbrAssignedDrivers();
                if (needed <= 0 || excessDrivers <= 0) continue;

                log.info("🧭 Evaluating under-provisioned zone [{}] needing {} driver(s)...",
                        underZone.getZoneName(), needed);

                double[] underCenter = GeoUtils.getCenter(underZone.getCoordinates());
                String destination = underCenter[0] + "," + underCenter[1];

                log.info("📍 Under-zone [{}] center calculated at [{}]", underZone.getZoneName(), destination);

                List<DriverZoneAssignment> closestDrivers = overDrivers.stream()
                        .filter(driver -> driver.hasValidLocation())
                        .sorted(Comparator.comparingDouble(driver -> {
                            String origin = driver.getLatitude() + "," + driver.getLongitude();
                            double distance = googleMapsService.getDrivingDistanceInKm(origin, destination);
                            log.debug("📐 Distance from driver [{}] to zone [{}] center: {} km",
                                    driver.getLivreurId(), underZone.getZoneName(), String.format("%.2f", distance));

                            return distance;
                        }))
                        .limit(needed)
                        .collect(Collectors.toList());

                for (DriverZoneAssignment driver : closestDrivers) {
                    String origin = driver.getLatitude() + "," + driver.getLongitude();
                    double distanceKm = googleMapsService.getDrivingDistanceInKm(origin, destination);

                    log.info("🚚 Reallocation Candidate: Move driver [{}] from [{}] to [{}] (Distance: {} km)",
                            driver.getLivreurId(), overZone.getZoneName(), underZone.getZoneName(), String.format("%.2f", distanceKm));


                    suggestions.add(String.format(
                            "🚚 Suggest reassigning driver [%s] from zone '%s' to zone '%s' (%.1f km driving distance)",
                            driver.getLivreurId(), overZone.getZoneName(), underZone.getZoneName(), distanceKm
                    ));
                }

                overDrivers.removeAll(closestDrivers);
                excessDrivers -= closestDrivers.size();
            }
        }

        log.info("✅ Driver reallocation suggestion process complete. {} suggestion(s) generated.", suggestions.size());

        return suggestions;
    }

    public List<RestaurantDTO> getAssignedRestaurantsByZone(String zoneId) {
        DeliveryZone zone = zoneRepo.findById(zoneId)
                .orElseThrow(() -> new ZoneNotFoundException("Zone not found"));
        return zone.getAssignedRestaurants() != null ? zone.getAssignedRestaurants() : new ArrayList<>();
    }
    public List<LivreurDTO> getAssignedDriversWithDetails(String zoneId) {
        List<DriverZoneAssignment> assignments = driverZoneAssignmentRepository.findByZoneIdAndAssignedTrue(zoneId);

        return assignments.stream()
                .map(assignment -> {
                    Optional<UserEntity> optionalDriver = userService.getUserById(assignment.getLivreurId());
                    UserEntity driver = optionalDriver.orElseThrow(() ->
                            new RuntimeException("Driver not found with ID: " + assignment.getLivreurId()));

                    return new LivreurDTO(
                            driver.getId(),
                            driver.getUsername(),  // assuming this corresponds to "id"
                            driver.getFirstname(),
                            driver.getLastname(),
                            driver.getEmail(),
                            driver.getPhoneNumber()
                    );
                })
                .collect(Collectors.toList());
    }


    public List<RestaurantDTO> getAvailableNonAssignedRestaurantsInZone(String zoneId) {
        DeliveryZone zone = zoneRepo.findById(zoneId)
                .orElseThrow(() -> new ZoneNotFoundException("Zone not found"));

        List<RestaurantDTO> unassignedRestaurants = restaurantClient.fetchAllRestaurants();

        // Log polygon points for debugging
        List<List<Double>> zonePolygon = zone.getCoordinates();
        log.info("🔵 Zone [{}] polygon points:", zone.getZoneName());
        zonePolygon.forEach(p -> log.info("  → Point: ({}, {})", p.get(0), p.get(1)));

        List<RestaurantDTO> availableInZone = unassignedRestaurants.stream()
                .filter(r -> {
                    boolean inside = GeoUtils.isPointInPolygon(r.getLongitude(), r.getLatitude(), zonePolygon);
                    log.debug("📍 Checking restaurant [{}] at ({}, {}) → inside: {}",
                            r.getNom(), r.getLongitude(), r.getLatitude(), inside);
                    return inside;
                })
                .collect(Collectors.toList());

        log.info("✅ Found {} available, non-assigned restaurants inside zone [{}]",
                availableInZone.size(), zone.getZoneName());

        return availableInZone;
    }


    public List<ZoneDistributionDTO> getRestaurantDistributionByZone() {
        return zoneRepo.findAll().stream()
                .map(zone -> new ZoneDistributionDTO(
                        zone.getZoneId(),
                        zone.getZoneName(),
                        zone.getNbrRestaurants()
                ))
                .collect(Collectors.toList());
    }

    public List<LivreurDTO> getAllUnassignedLivreurs() {
        List<LivreurEntity> unassignedLivreurs = userRepository.findByIsAssignedFalse();

        return unassignedLivreurs.stream()
                .map(driver -> new LivreurDTO(
                        driver.getId(), 
                        driver.getUsername(),
                        driver.getFirstname(),
                        driver.getLastname(),
                        driver.getEmail(),
                        driver.getPhoneNumber()
                ))
                .collect(Collectors.toList());
    }
    public List<DeliveryZoneSummaryDTO> getZonesWithAvailableCapacity() {
        log.info("🔍 Retrieving zones with available driver capacity...");

        List<DeliveryZoneSummaryDTO> zones = zoneRepo.findAll().stream()
                .filter(zone -> zone.getNbrAssignedDrivers() < zone.getMaxCapacity())
                .map(zone -> new DeliveryZoneSummaryDTO(zone.getZoneId(), zone.getZoneName()))
                .collect(Collectors.toList());

        log.info("✅ Found {} zones with available capacity", zones.size());

        return zones;
    }

    public List<DeliveryZoneSummaryDTO> getZones() {
        log.info("🔍 Retrieving zones ...");

        List<DeliveryZoneSummaryDTO> zones = zoneRepo.findAll().stream()

                .map(zone -> new DeliveryZoneSummaryDTO(zone.getZoneId(), zone.getZoneName()))
                .collect(Collectors.toList());

        log.info("✅ Found {} zones ", zones.size());

        return zones;
    }



}
