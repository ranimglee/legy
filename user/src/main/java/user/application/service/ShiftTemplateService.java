package user.application.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import user.application.dto.in.UpdateShiftTemplateRequest;
import user.domain.model.ShiftTemplate;
import user.domain.model.ShiftType;
import user.domain.repository.ShiftTemplateRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
@Service
@AllArgsConstructor
public class ShiftTemplateService {
    private final ShiftTemplateRepository shiftTemplateRepository;

    private static final Logger log = LoggerFactory.getLogger(ShiftService.class);


    public void ensureDefaultTemplatesInitialized() {
        if (shiftTemplateRepository.findAll().isEmpty()) {
            List<ShiftTemplate> defaultTemplates = List.of(
                    new ShiftTemplate(null, ShiftType.MORNING, LocalTime.of(8, 0), LocalTime.of(11, 30), 4),
                    new ShiftTemplate(null, ShiftType.PEAK_LUNCH, LocalTime.of(11, 30), LocalTime.of(14, 0), 8),
                    new ShiftTemplate(null, ShiftType.AFTERNOON, LocalTime.of(14, 0), LocalTime.of(18, 0), 4),
                    new ShiftTemplate(null, ShiftType.PEAK_DINNER, LocalTime.of(18, 30), LocalTime.of(22, 0), 10),
                    new ShiftTemplate(null, ShiftType.NIGHT, LocalTime.of(22, 0), LocalTime.of(1, 0), 3),
                    new ShiftTemplate(null, ShiftType.DAY_OFF, LocalTime.of(0, 0), LocalTime.of(23, 59), 2)
            );
            defaultTemplates.forEach(shiftTemplateRepository::save);
            log.info("Initialized default shift templates.");
        }
    }

    public ShiftTemplate updateShiftTemplate(String templateId, UpdateShiftTemplateRequest request) {
        ShiftTemplate template = shiftTemplateRepository.findById(templateId)
                .orElseThrow(() -> {
                    log.warn("Shift template not found with id {}", templateId);
                    return new NoSuchElementException("Shift template not found");
                });
        if (request.getShiftType() != null) {
            template.setShiftType(request.getShiftType());
        }
        if (request.getStartTime() != null) {
            template.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            template.setEndTime(request.getEndTime());
        }
        if (request.getMaxCouriers() != null) {
            template.setMaxCouriers(request.getMaxCouriers());
        }

        ShiftTemplate updatedTemplate = shiftTemplateRepository.save(template);
        log.info("Admin updated template {} with new times: {} - {}, maxCouriers: {}",
                templateId, template.getStartTime(), template.getEndTime(), template.getMaxCouriers());

        return updatedTemplate;
    }

    @Transactional
    public int deleteAllShiftTemplates() {
        long count = shiftTemplateRepository.count();
        shiftTemplateRepository.deleteAll();
        log.info("Deleted {} shift templates.", count);
        return (int) count;
    }

    @Transactional(readOnly = true)
    public List<ShiftTemplate> getAllShiftTemplates() {
        return shiftTemplateRepository.findAll();   // returns an empty list if none exist
    }
    /**
     * Delete a shift template by its ID
     */
    @Transactional
    public void deleteShiftTemplateById(String templateId) {
        if (!shiftTemplateRepository.existsById(templateId)) {
            log.warn("Attempted to delete non-existent shift template with ID: {}", templateId);
            throw new NoSuchElementException("Shift template not found with ID: " + templateId);
        }

        shiftTemplateRepository.deleteById(templateId);
        log.info("Deleted shift template with ID: {}", templateId);
    }

}
