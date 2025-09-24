package user.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shared.domain.service.NotificationService;
import user.application.dto.in.ShiftDTO;
import user.application.dto.in.UpdateShiftTimeRequest;
import user.application.dto.out.ShiftDayResponse;
import user.application.service.notification.FcmTokenService;
import user.application.service.notification.FirebaseNotificationService;
import user.domain.model.*;
import user.domain.repository.*;
import user.infrastructure.persistence.ZoneRepositoryImpl;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ShiftService {

    private final ShiftRepository shiftRepo;
    private final DriverZoneAssignmentRepository driverZoneAssignmentRepository;
    private final ShiftTemplateService shiftTemplateService;
    private final ShiftTemplateRepository shiftTemplateRepository;
    private final UserRepository userRepository;
    private final FcmTokenService notificationService;
    private final DeliveryZoneRepository zoneRepository;

    private static final Logger log = LoggerFactory.getLogger(ShiftService.class);

    public List<ShiftDayResponse> getShiftsForZoneWeek(String zoneId, LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        log.info("Fetching weekly shifts for zone {} between {} and {}", zoneId, weekStart, weekEnd);

        List<Shift> shifts = shiftRepo.findShiftsByZoneAndDateBetween(zoneId, weekStart, weekEnd);
        log.info("Loaded {} shifts for zone {}", shifts.size(), zoneId);

        Map<LocalDate, List<Shift>> grouped = shifts.stream()
                .collect(Collectors.groupingBy(Shift::getDate));

        List<ShiftDayResponse> result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            List<ShiftDTO> shiftDtos = grouped.getOrDefault(date, Collections.emptyList()).stream()
                    .filter(Shift::isValidTimeRange)
                    .map(shift -> new ShiftDTO(
                            shift.getShiftId(),
                            shift.getShiftType(),
                            shift.getStartTime(),
                            shift.getEndTime(),
                            shift.getDuration(),
                            shift.isAvailable(),
                            shift.getMaxCouriers(),
                            shift.getAssignedCount()
                    ))
                    .collect(Collectors.toList());

            result.add(new ShiftDayResponse(
                    date.getDayOfWeek().name(),
                    date.toString(),
                    shiftDtos
            ));
        }

        log.info("Final weekly shift response size: {}", result.size());
        return result;
    }

    public List<Shift> generateShiftsForWeek(String zoneId, LocalDate startDate) {
        shiftTemplateService.ensureDefaultTemplatesInitialized();
        List<Shift> createdShifts = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            createdShifts.addAll(generateShiftsForDay(zoneId, currentDate));
        }

        // Send push notifications to drivers if shifts were created
        if (!createdShifts.isEmpty()) {
            notifyDrivers(zoneId, startDate, createdShifts);
        }

        return createdShifts;
    }

    private List<Shift> generateShiftsForDay(String zoneId, LocalDate date) {
        Set<ShiftType> existingTypes = shiftRepo.findShiftTypesByZoneAndDate(zoneId, date);
        List<ShiftTemplate> templates = shiftTemplateRepository.findAll();
        List<Shift> createdShifts = new ArrayList<>();

        for (ShiftTemplate template : templates) {
            if (!existingTypes.contains(template.getShiftType())) {
                Shift shift = new Shift(
                        null,
                        template.getMaxCouriers(),
                        0,
                        template.getShiftType(),
                        date,
                        template.getStartTime(),
                        template.getEndTime(),
                        new ArrayList<>(),
                        zoneId
                );
                createdShifts.add(shiftRepo.save(shift));
                log.info("Saved shift type {} on date {}", template.getShiftType(), date);
            }
        }

        return createdShifts;
    }

    private void notifyDrivers(String zoneId, LocalDate startDate, List<Shift> shifts) {
        Optional<DeliveryZone> zone = zoneRepository.findById(zoneId);
        String zoneName = zone.map(DeliveryZone::getZoneName).orElse(zoneId);

        List<DriverZoneAssignment> assignments = driverZoneAssignmentRepository.findByZoneIdAndAssignedTrue(zoneId);
        if (assignments.isEmpty()) {
            log.info("No assigned drivers found in zone {} to notify", zoneId);
            return;
        }

        List<String> livreurIds = assignments.stream()
                .map(DriverZoneAssignment::getLivreurId)
                .toList();

        log.info("🔎 Livreur IDs: {}", livreurIds);

        List<LivreurEntity> drivers = userRepository.findByIdInAndStatus(livreurIds, Status.ACTIVE);

        log.info("🔄 Drivers found: {}", drivers.stream().map(UserEntity::getId).toList());

        String title = "New Shifts Available!";
        String body = String.format(
                "New shifts for the week starting %s are available in %s. Check the app to request your shifts now!",
                startDate.toString(),
                zoneName
        );
        String deepLink = String.format("app://shifts?zone=%s&week=%s", zoneId, startDate.toString());
        for (UserEntity driver : drivers) {
            String rawFcmToken = driver.getFcmToken();
            String cleanToken = null;

            if (rawFcmToken != null && !rawFcmToken.trim().isEmpty()) {
                try {
                    JSONObject json = new JSONObject(rawFcmToken);
                    cleanToken = json.optString("token", null); // renvoie null si pas présent
                } catch (Exception e) {
                    // rawFcmToken n’est pas un JSON, on suppose que c’est un token brut
                    cleanToken = rawFcmToken;
                }
            }

            if (cleanToken != null && !cleanToken.trim().isEmpty()) {
                try {
                    notificationService.sendToDriver(
                            cleanToken,
                            title,
                            body,
                            deepLink
                    );
                    log.info("✅ Sent notification to driver {} for new shifts in zone {}", driver.getId(), zoneId);
                } catch (Exception e) {
                    log.error("❌ Failed to send notification to driver {}: {}", driver.getId(), e.getMessage(), e);
                }
            } else {
                log.warn("⚠️ Driver {} has no valid FCM token, skipping", driver.getId());
            }
        }

    }


    public Shift createManualShift(String zoneId, Shift shift) {
        if (shiftRepo.existsByZoneIdAndDateAndShiftType(zoneId, shift.getDate(), shift.getShiftType())) {
            throw new IllegalStateException("Shift already exists for this zone and type on this date.");
        }
        shift.setZoneId(zoneId);
        return shiftRepo.save(shift);
    }

    @Cacheable(value = "weeklyShifts", key = "#livreurId + '-' + #weekStart")
    public List<ShiftDayResponse> getShiftsForLivreurWeek(LocalDate weekStart, String livreurId) {
        LocalDate weekEnd = weekStart.plusDays(6);
        log.info("Fetching shifts for livreur {} between {} and {}", livreurId, weekStart, weekEnd);

        List<DriverZoneAssignment> assignments = driverZoneAssignmentRepository.findByLivreurId(livreurId);

        if (assignments.isEmpty()) {
            log.warn("No assignments found for livreur {}", livreurId);
            throw new NoSuchElementException("Livreur is not assigned to any zone");
        }

        DriverZoneAssignment assignment = assignments.stream()
                .filter(DriverZoneAssignment::isAssigned)
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Livreur {} has no active zone assignment", livreurId);
                    return new IllegalStateException("Livreur has no active zone assignment");
                });

        String zoneId = assignment.getZoneId();

        List<Shift> shifts = shiftRepo.findShiftsByZoneAndDateBetween(zoneId, weekStart, weekEnd);
        log.info("Loaded {} shifts for zone {}", shifts.size(), zoneId);

        Map<LocalDate, List<Shift>> grouped = shifts.stream()
                .collect(Collectors.groupingBy(Shift::getDate));

        List<ShiftDayResponse> result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            List<ShiftDTO> shiftDtos = grouped.getOrDefault(date, Collections.emptyList()).stream()
                    .filter(Shift::isValidTimeRange)
                    .map(shift -> new ShiftDTO(
                            shift.getShiftId(),
                            shift.getShiftType(),
                            shift.getStartTime(),
                            shift.getEndTime(),
                            shift.getDuration(),
                            shift.isAvailable(),
                            shift.getMaxCouriers(),
                            shift.getAssignedCount()
                    ))
                    .collect(Collectors.toList());

            result.add(new ShiftDayResponse(
                    date.getDayOfWeek().name(),
                    date.toString(),
                    shiftDtos
            ));
        }

        log.info("Final weekly shift response size: {}", result.size());
        return result;
    }

    public Shift updateShiftTime(String shiftId, UpdateShiftTimeRequest request) {
        Shift shift = shiftRepo.findById(shiftId)
                .orElseThrow(() -> {
                    log.warn("Shift not found with id {}", shiftId);
                    return new NoSuchElementException("Shift not found");
                });

        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new IllegalArgumentException("Start time and end time must be provided");
        }

        if (request.getStartTime().equals(request.getEndTime())) {
            throw new IllegalArgumentException("Start time and end time cannot be the same");
        }

        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());
        shift.setMaxCouriers(request.getMaxCouriers());

        Shift updatedShift = shiftRepo.save(shift);

        log.info("Admin updated shift {} times to {} - {}", shiftId, request.getStartTime(), request.getEndTime());

        return updatedShift;
    }


    @Transactional
    public int deleteWeeklyShifts(String zoneId, LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);

        List<Shift> shiftsToDelete = shiftRepo.findShiftsByZoneAndDateBetween(zoneId, weekStart, weekEnd);

        if (shiftsToDelete.isEmpty()) {
            log.warn("No shifts found to delete for zone {} between {} and {}", zoneId, weekStart, weekEnd);
            return 0;
        }

        shiftRepo.deleteAllById(shiftsToDelete.stream()
                .map(Shift::getShiftId)
                .collect(Collectors.toList()));

        log.info("Deleted {} shifts for zone {} between {} and {}", shiftsToDelete.size(), zoneId, weekStart, weekEnd);

        return shiftsToDelete.size();
    }




}
