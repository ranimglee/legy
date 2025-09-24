package user.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import user.application.dto.in.LivreurDTO;
import user.application.dto.in.ShiftRequestDTO;
import user.application.dto.in.ShiftRequestWithLivreurDTO;
import user.application.dto.in.WeeklyShiftRequestView;
import user.application.dto.out.BatchShiftResult;
import user.domain.model.*;
import user.domain.repository.DriverZoneAssignmentRepository;
import user.domain.repository.ShiftRepository;
import user.domain.repository.ShiftRequestRepository;
import user.domain.service.UserService;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShiftRequestService {
    private final RedisTemplate<String, String> redisTemplate;
    private final UserService userService;
    private final ShiftRequestRepository shiftRequestRepository;
    private final ShiftRepository shiftRepo;
    private final DriverZoneAssignmentRepository driverZoneAssignmentRepository;
    private static final Logger log = LoggerFactory.getLogger(ShiftService.class);

    public ShiftRequestService(@Qualifier("redisTemplateConnected") RedisTemplate<String, String> redisTemplate, UserService userService, ShiftRequestRepository shiftRequestRepository, ShiftRepository shiftRepo, DriverZoneAssignmentRepository driverZoneAssignmentRepository) {
        this.redisTemplate = redisTemplate;
        this.userService = userService;
        this.shiftRequestRepository = shiftRequestRepository;
        this.shiftRepo = shiftRepo;
        this.driverZoneAssignmentRepository = driverZoneAssignmentRepository;
    }



    private void saveNotificationToRedis(String livreurId, String shiftId, String status) {
        String key = "user:" + livreurId + ":notifications";
        String message = String.format("{\"shiftId\":\"%s\",\"status\":\"%s\",\"timestamp\":\"%s\"}",
                shiftId, status, java.time.Instant.now().toString());
        redisTemplate.opsForList().rightPush(key, message);
    }



    public ShiftRequest bookShift(ShiftRequestDTO dto, String authenticatedLivreurId) {
        Shift shift = shiftRepo.findById(dto.getShiftId())
                .orElseThrow(() -> new IllegalArgumentException("Shift not found"));

        LocalDate shiftDate = shift.getDate();

        List<ShiftRequest> existingRequestsForDate = shiftRequestRepository.findByLivreurIdAndShiftDate(authenticatedLivreurId, shiftDate);

        boolean dayOffExistsForDate = existingRequestsForDate.stream()
                .anyMatch(req -> req.getShift().getShiftType() == ShiftType.DAY_OFF);

        // 1️⃣ If trying to book DAY_OFF but already has one → reject
        if (shift.getShiftType() == ShiftType.DAY_OFF && dayOffExistsForDate) {
            throw new IllegalStateException("Already marked day off for this date.");
        }

        // 2️⃣ If trying to book work shift on DAY_OFF → cancel the DAY_OFF
        if (shift.getShiftType() != ShiftType.DAY_OFF && dayOffExistsForDate) {
            log.info("Cancelling existing DAY_OFF for {} on {}", authenticatedLivreurId, shiftDate);
            existingRequestsForDate.stream()
                    .filter(req -> req.getShift().getShiftType() == ShiftType.DAY_OFF)
                    .forEach(req -> {
                        req.setStatus(ShiftStatus.CANCELLED);
                        shiftRequestRepository.save(req);
                    });
        }

        // 3️⃣ Check if already has a DAY_OFF this week (only one allowed)
        LocalDate weekStart = shiftDate.with(java.time.DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);

        boolean dayOffExistsForWeek = shiftRequestRepository.findByLivreurIdAndShiftDateBetween(authenticatedLivreurId, weekStart, weekEnd)
                .stream()
                .anyMatch(req -> req.getShift().getShiftType() == ShiftType.DAY_OFF);

        if (shift.getShiftType() == ShiftType.DAY_OFF && dayOffExistsForWeek) {
            throw new IllegalStateException("Only one day off allowed per week.");
        }

        // 4️⃣ Standard zone + availability checks
        DriverZoneAssignment assignment = driverZoneAssignmentRepository
                .findByLivreurIdAndZoneId(authenticatedLivreurId, shift.getZoneId())
                .orElseThrow(() -> {
                    log.warn("Driver {} is not assigned to zone {}", authenticatedLivreurId, shift.getZoneId());
                    return new IllegalArgumentException("Driver is not assigned to this zone");
                });

        if (!assignment.isAssigned()) {
            throw new IllegalArgumentException("Driver is not currently assigned to this zone");
        }

        if (!shift.isAvailable()) {
            throw new IllegalStateException("Shift is full");
        }

        boolean alreadyRequested = shiftRequestRepository.findByLivreurIdAndShiftId(authenticatedLivreurId, dto.getShiftId()).isPresent();
        if (alreadyRequested) {
            throw new IllegalStateException("Already requested");
        }

        // 5️⃣ Save the new shift request
        ShiftRequest request = new ShiftRequest();
        request.setLivreurId(authenticatedLivreurId);
        request.setZoneId(shift.getZoneId());
        request.setShift(shift);
        request.setStatus(ShiftStatus.PENDING);

        return shiftRequestRepository.save(request);
    }


    public void cancelWeeklyShiftRequests(String livreurId, LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);

        List<ShiftRequest> requests = shiftRequestRepository.findByLivreurIdAndShiftDateBetween(livreurId, weekStart, weekEnd);

        if (requests.isEmpty()) {
            log.warn("No shift requests found to cancel for livreur {} between {} and {}", livreurId, weekStart, weekEnd);
            return;
        }


        for (ShiftRequest req : requests) {
            if (req.getId() == null) {
                log.warn("⚠️ Skipping request with missing ID on {}", req.getShift().getDate());
                continue;
            }

            if (req.getStatus() != ShiftStatus.PENDING) {
                log.info("⏩ Request {} on {} is {} and cannot be deleted by the driver.",
                        req.getId(), req.getShift().getDate(), req.getStatus().name());
                continue;
            }

            shiftRequestRepository.deleteById(req.getId());
            log.info("🗑️ Deleted pending shift request {} on {}", req.getId(), req.getShift().getDate());
        }

        log.info("🗑️🗑️ Total pending weekly shift requests deleted for livreur {}:", livreurId);
    }


    public BatchShiftResult bookMultipleShifts(List<String> shiftIds, String livreurId) {
        Map<LocalDate, List<Shift>> shiftsByDate = new HashMap<>();
        int dayOffCount = 0;

        for (String shiftId : shiftIds) {
            Shift shift = shiftRepo.findById(shiftId)
                    .orElseThrow(() -> new IllegalArgumentException("Shift not found"));
            shiftsByDate.computeIfAbsent(shift.getDate(), k -> new ArrayList<>()).add(shift);
        }

        for (Map.Entry<LocalDate, List<Shift>> entry : shiftsByDate.entrySet()) {
            boolean hasDayOff = entry.getValue().stream().anyMatch(s -> s.getShiftType() == ShiftType.DAY_OFF);
            boolean hasWorkShift = entry.getValue().stream().anyMatch(s -> s.getShiftType() != ShiftType.DAY_OFF);

            if (hasDayOff && hasWorkShift) {
                throw new IllegalStateException("Cannot request both DAY_OFF and work shifts on " + entry.getKey());
            }

            if (hasDayOff) {
                dayOffCount++;
            }
        }

        if (dayOffCount > 1) {
            throw new IllegalStateException("Only one DAY_OFF allowed per week.");
        }

        // If validation passes, proceed to book
        List<String> successful = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (String shiftId : shiftIds) {
            try {
                bookShift(new ShiftRequestDTO(shiftId), livreurId);
                successful.add(shiftId);
            } catch (Exception e) {
                log.warn("Failed to book shift {}: {}", shiftId, e.getMessage());
            }
        }

        return new BatchShiftResult(successful, failed);
    }

    public List<WeeklyShiftRequestView> getWeeklyShiftRequests(LocalDate weekStart, String livreurId) {
        LocalDate weekEnd = weekStart.plusDays(6);
        List<ShiftRequest> requests = shiftRequestRepository.findByLivreurIdAndShiftDateBetween(livreurId, weekStart, weekEnd);

        // Group multiple requests per day
        Map<LocalDate, List<ShiftRequest>> dayToRequests = requests.stream()
                .collect(Collectors.groupingBy(req -> req.getShift().getDate()));

        List<WeeklyShiftRequestView> result = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            List<ShiftRequest> reqsForDay = dayToRequests.getOrDefault(date, Collections.emptyList());

            if (!reqsForDay.isEmpty()) {
                for (ShiftRequest req : reqsForDay) {
                    result.add(new WeeklyShiftRequestView(
                            date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRENCH).toUpperCase(),
                            date.toString(),
                            req.getShift().getShiftType().name(),
                            req.getStatus().name(),
                            req.getShift().getStartTime() + " - " + req.getShift().getEndTime(),
                            req.getShift().getMaxCouriers(),
                            req.getShift().getAssignedCount()
                    ));
                }
            } else {
                result.add(new WeeklyShiftRequestView(
                        date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRENCH).toUpperCase(),
                        date.toString(),
                        "Non défini",
                        "NONE",
                        "",
                        0,
                        0
                ));
            }
        }

        return result;
    }

    public void approveWeeklyShifts(String livreurId, LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);

        List<ShiftRequest> requests = shiftRequestRepository.findByLivreurIdAndShiftDateBetween(livreurId, weekStart, weekEnd);

        if (requests.isEmpty()) {
            log.warn("No shift requests found for livreur {} between {} and {}", livreurId, weekStart, weekEnd);
            return;
        }

        for (ShiftRequest req : requests) {
            if (req.getId() == null) {
                log.warn("⚠️ Skipping request with missing ID on {}", req.getShift().getDate());
                continue;
            }

            Optional<ShiftRequest> existingReqOpt = shiftRequestRepository.findById(req.getId());
            if (existingReqOpt.isPresent()) {
                ShiftRequest toUpdateReq = existingReqOpt.get();
                Shift shift = shiftRepo.findById(toUpdateReq.getShift().getShiftId())
                        .orElseThrow(() -> new IllegalStateException("Shift not found"));

                if (!shift.getDriverIds().contains(livreurId)) {
                    shift.getDriverIds().add(livreurId);
                    shift.setAssignedCount(shift.getAssignedCount() + 1);
                    shiftRepo.save(shift);
                    log.info("✅ Added livreur {} to shift {}", livreurId, shift.getShiftId());
                }

                toUpdateReq.setStatus(ShiftStatus.APPROVED);
                shiftRequestRepository.save(toUpdateReq);
                log.info("✅ Approved shift request {} on {}", toUpdateReq.getId(), toUpdateReq.getShift().getDate());
            } else {
                log.warn("⚠️ Could not find existing shift request with ID {}", req.getId());
            }
        }

        log.info("✅✅ All weekly shift requests approved for livreur {}", livreurId);
    }


    public void rejectWeeklyShifts(String livreurId, LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);

        List<ShiftRequest> requests = shiftRequestRepository.findByLivreurIdAndShiftDateBetween(livreurId, weekStart, weekEnd);

        if (requests.isEmpty()) {
            log.warn("No shift requests found for livreur {} between {} and {}", livreurId, weekStart, weekEnd);
            return;
        }

        for (ShiftRequest req : requests) {
            if (req.getId() == null) {
                log.warn("⚠️ Skipping request with missing ID on {}", req.getShift().getDate());
                continue;
            }

            Optional<ShiftRequest> existingReqOpt = shiftRequestRepository.findById(req.getId());
            if (existingReqOpt.isPresent()) {
                ShiftRequest toUpdateReq = existingReqOpt.get();
                Shift shift = shiftRepo.findById(toUpdateReq.getShift().getShiftId())
                        .orElseThrow(() -> new IllegalStateException("Shift not found"));

                if (shift.getDriverIds().contains(livreurId)) {
                    shift.getDriverIds().remove(livreurId);
                    shift.setAssignedCount(Math.max(shift.getAssignedCount() - 1, 0));
                    shiftRepo.save(shift);
                    log.info("❌ Removed livreur {} from shift {}", livreurId, shift.getShiftId());
                }

                toUpdateReq.setStatus(ShiftStatus.REJECTED);
                shiftRequestRepository.save(toUpdateReq);
                log.info("❌ Rejected shift request {} on {}", toUpdateReq.getId(), toUpdateReq.getShift().getDate());
            } else {
                log.warn("⚠️ Could not find existing shift request with ID {}", req.getId());
            }
        }

        log.info("❌❌ All weekly shift requests rejected for livreur {}", livreurId);
    }


    public void approveSingleWeeklyShiftRequest(String livreurId, String shiftRequestId) {
        Optional<ShiftRequest> requestOpt = shiftRequestRepository.findById(shiftRequestId);

        if (requestOpt.isEmpty()) {
            log.warn("⚠️ Shift request {} not found for approval.", shiftRequestId);
            throw new IllegalArgumentException("Shift request not found.");
        }

        ShiftRequest request = requestOpt.get();

        if (!request.getLivreurId().equals(livreurId)) {
            log.warn("⚠️ Shift request {} does not belong to livreur {}.", shiftRequestId, livreurId);
            throw new IllegalArgumentException("Shift request does not belong to this driver.");
        }

        if (request.getStatus() != ShiftStatus.PENDING) {
            log.info("⏩ Shift request {} is in status {} and cannot be approved.", shiftRequestId, request.getStatus().name());
            throw new IllegalStateException("Only pending requests can be approved.");
        }

        Shift shift = shiftRepo.findById(request.getShift().getShiftId())
                .orElseThrow(() -> new IllegalStateException("Shift not found."));

        if (!shift.getDriverIds().contains(livreurId)) {
            shift.getDriverIds().add(livreurId);
            shift.setAssignedCount(shift.getAssignedCount() + 1);
            shiftRepo.save(shift);
            log.info("✅ Added livreur {} to shift {}", livreurId, shift.getShiftId());
        }

        request.setStatus(ShiftStatus.APPROVED);
        shiftRequestRepository.save(request);
        log.info("✅ Approved single shift request {} for livreur {} on {}", shiftRequestId, livreurId, request.getShift().getDate());
    }

    public void approveAllWeeklyShiftRequestsForDriver(String livreurId, LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);

        List<ShiftRequest> requests = shiftRequestRepository.findByLivreurIdAndShiftDateBetween(livreurId, weekStart, weekEnd);

        if (requests.isEmpty()) {
            log.warn("No shift requests found to approve for livreur {} between {} and {}", livreurId, weekStart, weekEnd);
            return;
        }


        for (ShiftRequest req : requests) {
            if (req.getId() == null) {
                log.warn("⚠️ Skipping request with missing ID on {}", req.getShift().getDate());
                continue;
            }

            if (req.getStatus() != ShiftStatus.PENDING) {
                log.info("⏩ Request {} on {} is {} and cannot be approved.",
                        req.getId(), req.getShift().getDate(), req.getStatus().name());
                continue;
            }

            Shift shift = shiftRepo.findById(req.getShift().getShiftId())
                    .orElseThrow(() -> new IllegalStateException("Shift not found."));

            if (!shift.getDriverIds().contains(livreurId)) {
                shift.getDriverIds().add(livreurId);
                shift.setAssignedCount(shift.getAssignedCount() + 1);
                shiftRepo.save(shift);
                log.info("✅ Added livreur {} to shift {}", livreurId, shift.getShiftId());
            }

            req.setStatus(ShiftStatus.APPROVED);
            shiftRequestRepository.save(req);
            log.info("✅ Approved shift request {} on {}", req.getId(), req.getShift().getDate());
        }

        log.info("✅✅ Total weekly shift requests approved for livreur {}: ", livreurId);
    }

    public List<ShiftRequestWithLivreurDTO> getAllShiftRequestsWithLivreurs() {
        List<ShiftRequest> requests = shiftRequestRepository.findAll();

        return requests.stream()
                .map(req -> {
                    LivreurDTO livreur = userService.getLivreurDetails(req.getLivreurId());
                    return new ShiftRequestWithLivreurDTO(
                            req.getId(),
                            req.getStatus().name(),
                            req.getShift(),
                            livreur
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all drivers who made shift requests with PENDING status for a specific week.
     *
     * @param weekStart The start date of the week (typically a Monday)
     * @return List of LivreurDTO containing details of drivers with pending shift requests
     */
    public List<LivreurDTO> getDriversWithPendingRequestsByWeek(LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);

        // Fetch all shift requests for the week with PENDING status
        List<ShiftRequest> pendingRequests = shiftRequestRepository
                .findByShiftDateBetweenAndStatus(weekStart, weekEnd, ShiftStatus.PENDING);

        if (pendingRequests.isEmpty()) {
            log.info("No pending shift requests found for week starting {}", weekStart);
            return Collections.emptyList();
        }

        // Get unique driver IDs and map to LivreurDTO
        return pendingRequests.stream()
                .map(ShiftRequest::getLivreurId)
                .distinct()
                .map(livreurId -> {
                    try {
                        return userService.getLivreurDetails(livreurId);
                    } catch (Exception e) {
                        log.warn("Failed to fetch driver details for livreurId {}: {}",
                                livreurId, e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
