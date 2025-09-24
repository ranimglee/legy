package user.adapters.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import user.application.dto.in.*;
import user.application.dto.out.BatchShiftResult;
import user.application.service.ShiftRequestService;
import user.domain.model.ShiftRequest;
import user.infrastructure.security.JwtUtilImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
@Slf4j
public class ShiftRequestController {

    private final ShiftRequestService shiftRequestService;
    private final JwtUtilImpl jwtUtil;

    @PostMapping("/livreur/request")
    public ResponseEntity<ShiftRequest> requestShift(
            @RequestBody ShiftRequestDTO dto,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String livreurId = jwtUtil.extractUserIdFromAccessToken(token);

        ShiftRequest shiftRequest = shiftRequestService.bookShift(dto, livreurId);
        return ResponseEntity.ok(shiftRequest);
    }
    @PostMapping("/livreur/request/batch")
    public ResponseEntity<Map<String, Object>> requestBatchShifts(
            @RequestBody BatchShiftRequestDTO dto,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String livreurId = jwtUtil.extractUserIdFromAccessToken(token);

        BatchShiftResult result = shiftRequestService.bookMultipleShifts(dto.getShiftIds(), livreurId);


        Map<String, Object> response = Map.of(
                "bookedShifts", result,
                "daysOff", dto.getDaysOff() != null ? dto.getDaysOff() : List.of()
        );


        return ResponseEntity.ok(response);
    }

    @GetMapping("/livreur/requests")
    public ResponseEntity<List<WeeklyShiftRequestView>> getLivreurShiftRequests(
            @RequestParam(name = "weekStart") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String livreurId = jwtUtil.extractUserIdFromAccessToken(token);

        List<WeeklyShiftRequestView> planning = shiftRequestService.getWeeklyShiftRequests(weekStart, livreurId);
        return ResponseEntity.ok(planning);
    }
    @PostMapping("/moderateur/{livreurId}/approve-week")
    public ResponseEntity<String> approveWeek(
            @PathVariable String livreurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {

        shiftRequestService.approveWeeklyShifts(livreurId, weekStart);
        return ResponseEntity.ok("Weekly shift requests approved.");
    }

    @PostMapping("/moderateur/{livreurId}/reject-week")
    public ResponseEntity<String> rejectWeek(@PathVariable String livreurId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        shiftRequestService.rejectWeeklyShifts(livreurId, weekStart);
        return ResponseEntity.ok("Weekly shift requests rejected.");
    }
    @DeleteMapping("/moderateur/weekly-cancel/{livreurId}")
    public ResponseEntity<Map<String, Object>> cancelWeeklyShiftRequests(
            @RequestParam(name = "weekStart") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart,
            @PathVariable String livreurId
            ) {
        shiftRequestService.cancelWeeklyShiftRequests(livreurId, weekStart);

        return ResponseEntity.ok(Map.of(
                "livreurId", livreurId,
                "weekStart", weekStart,
                "message", "Pending weekly shift requests cancelled successfully."
        ));
    }

    @PostMapping("/moderateur/approve-single")
    public ResponseEntity<Map<String, String>> approveSingleShiftRequest(
            @RequestParam String livreurId,
            @RequestParam String shiftRequestId
    ) {
        shiftRequestService.approveSingleWeeklyShiftRequest(livreurId, shiftRequestId);

        return ResponseEntity.ok(Map.of(
                "message", "Shift request approved successfully.",
                "livreurId", livreurId,
                "shiftRequestId", shiftRequestId
        ));
    }
    @PostMapping("/moderateur/approve-weekly")
    public ResponseEntity<Map<String, Object>> approveAllWeeklyShiftRequests(
            @RequestParam String livreurId,
            @RequestParam(name = "weekStart") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart
    ) {


        shiftRequestService.approveAllWeeklyShiftRequestsForDriver(livreurId, weekStart);

        return ResponseEntity.ok(Map.of(
                "message", "All pending weekly shift requests approved successfully.",
                "livreurId", livreurId,
                "weekStart", weekStart
        ));
    }


    @GetMapping("/moderateur/get-shift-requests")
    public List<ShiftRequestWithLivreurDTO> getAllShiftRequestsByDate(
            ) {
        return shiftRequestService.getAllShiftRequestsWithLivreurs();
    }

    /**
     * Retrieves all drivers with pending shift requests for a specific week.
     *
     * @param weekStart The start date of the week (typically a Monday) in YYYY-MM-DD format
     * @return List of LivreurDTO containing details of drivers with pending shift requests
     */
    @GetMapping("/drivers/pending")
    public ResponseEntity<List<LivreurDTO>> getDriversWithPendingRequests(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        List<LivreurDTO> drivers = shiftRequestService.getDriversWithPendingRequestsByWeek(weekStart);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/weekly-shifts-by-livreur")
    public List<WeeklyShiftRequestView> getWeeklyShiftRequests(
            @RequestParam String livreurId,
            @RequestParam String weekStart
    ) {
        log.info("📥 Getting weekly shift requests for livreurId={} from weekStart={}", livreurId, weekStart);
        LocalDate startDate = LocalDate.parse(weekStart); // format: YYYY-MM-DD
        return shiftRequestService.getWeeklyShiftRequests(startDate, livreurId);
    }
}
