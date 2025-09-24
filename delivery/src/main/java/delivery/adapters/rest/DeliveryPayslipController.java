package delivery.adapters.rest;

import delivery.application.dto.in.DeliveryPersonPayslip;
import delivery.application.service.DeliveryPayslipService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/financier/deliveries/payslip")
@AllArgsConstructor
public class DeliveryPayslipController {

    private final DeliveryPayslipService payslipService;



    /**
     * Get payslip for a delivery person between two dates.
     *
     * Example: GET /api/deliveries/payslip/{deliveryPersonId}?startDate=2025-07-01&endDate=2025-07-20
     */
    @GetMapping("/{deliveryPersonId}")
    public ResponseEntity<DeliveryPersonPayslip> getPayslip(
            @PathVariable String deliveryPersonId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate
    ) {
        DeliveryPersonPayslip payslip = payslipService.generatePayslip(deliveryPersonId, startDate, endDate);
        if (payslip == null || payslip.totalDeliveries() == 0) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(payslip);
    }
}
