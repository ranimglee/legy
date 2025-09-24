package user.adapters.rest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import user.application.dto.in.ShiftDTO;
import user.application.dto.in.UpdateShiftTemplateRequest;
import user.application.dto.in.UpdateShiftTimeRequest;
import user.application.dto.out.ShiftDayResponse;
import user.application.service.ShiftService;
import user.application.service.ShiftTemplateService;
import user.domain.model.Shift;
import user.domain.model.ShiftTemplate;
import user.infrastructure.security.JwtUtilImpl;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
@Slf4j
public class ShiftController {


    private final ShiftService shiftService;
    private final ShiftTemplateService shiftTemplateService;

    private final JwtUtilImpl jwtUtil;

    /**
     * Get all shifts for a given zone and date
     */
    @GetMapping("/get-weekly-shifts-zone/{zoneId}")
    public ResponseEntity<List<ShiftDayResponse>> getWeeklyShiftsForZone(
            @PathVariable String zoneId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart
    ) {
        List<ShiftDayResponse> shifts = shiftService.getShiftsForZoneWeek(zoneId, weekStart);
        return ResponseEntity.ok(shifts);
    }

    @PostMapping("/generate-week")
    public ResponseEntity<?> generateShiftsForWeek(
            @RequestParam String zoneId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        try {
            List<Shift> shifts = shiftService.generateShiftsForWeek(zoneId, startDate);
            return ResponseEntity.ok(shifts);
        } catch (Exception ex) {
            log.error("Failed to generate shifts for zone {} starting on {}: {}", zoneId, startDate, ex.getMessage(), ex);
            return ResponseEntity.status(500).body("Internal Server Error: " + ex.getMessage());
        }
    }


    /**
     * Create a manual shift (used by moderator)
     */
    @PostMapping("/create-shift-manually/{zoneId}")
    public ResponseEntity<Shift> createManualShift(
            @RequestParam String zoneId,
            @RequestBody Shift shift) {
        Shift created = shiftService.createManualShift(zoneId, shift);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/livreur/upcoming")
    public ResponseEntity<List<ShiftDayResponse>> getShiftsForLivreur(
            @RequestParam(name = "weekStart") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart,
            @RequestHeader(name = "Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");
        String livreurId = jwtUtil.extractUserIdFromAccessToken(token);

        List<ShiftDayResponse> shifts = shiftService.getShiftsForLivreurWeek(weekStart, livreurId);
        return ResponseEntity.ok(shifts);
    }

    @PatchMapping("/update-time/{shiftId}")
    public ResponseEntity<ShiftDTO> updateShiftTime(
            @PathVariable String shiftId,
            @RequestBody UpdateShiftTimeRequest request
    ) {
        Shift updatedShift = shiftService.updateShiftTime(shiftId, request);

        ShiftDTO dto = new ShiftDTO(
                updatedShift.getShiftId(),
                updatedShift.getShiftType(),
                updatedShift.getStartTime(),
                updatedShift.getEndTime(),
                updatedShift.getDuration(),
                updatedShift.isAvailable(),
                updatedShift.getMaxCouriers(),
                updatedShift.getAssignedCount()
        );

        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/update-shift-template/{templateId}")
    public ResponseEntity<ShiftTemplate> updateShiftTemplate(
            @PathVariable String templateId,
            @RequestBody UpdateShiftTemplateRequest request
    ) {
        ShiftTemplate updated = shiftTemplateService.updateShiftTemplate(templateId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete-week")
    public ResponseEntity<String> deleteWeeklyShifts(
            @RequestParam String zoneId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart
    ) {
        int deletedCount = shiftService.deleteWeeklyShifts(zoneId, weekStart);

        if (deletedCount == 0) {
            return ResponseEntity.ok("No shifts found to delete for zone " + zoneId + " starting on " + weekStart);
        }

        return ResponseEntity.ok("Deleted " + deletedCount + " shifts for zone " + zoneId + " starting on " + weekStart);
    }

    @DeleteMapping("/delete-shift-template")
    public ResponseEntity<String> deleteAllShiftTemplates() {
        int deletedCount = shiftTemplateService.deleteAllShiftTemplates();

        if (deletedCount == 0) {
            return ResponseEntity.ok("No shift templates found to delete.");
        }

        return ResponseEntity.ok("Deleted " + deletedCount + " shift templates.");
    }

    @GetMapping("/get-shift-template")
    public ResponseEntity<List<ShiftTemplate>> getAllTemplates() {
        List<ShiftTemplate> templates = shiftTemplateService.getAllShiftTemplates();
        return ResponseEntity.ok(templates);
    }
    @DeleteMapping("/delete/shift-templates/{id}")
    public ResponseEntity<Void> deleteShiftTemplate(@PathVariable String id) {
        shiftTemplateService.deleteShiftTemplateById(id);
        return ResponseEntity.noContent().build();
    }


}