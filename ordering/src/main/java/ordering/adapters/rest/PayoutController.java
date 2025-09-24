package ordering.adapters.rest;

import lombok.RequiredArgsConstructor;
import ordering.application.dto.restoPayout.RestaurantPayslipDTO;
import ordering.application.dto.restoPayout.PayoutMapperDto;
import ordering.application.dto.restoPayout.PayoutSummaryDTO;
import ordering.domain.model.Payout;
import ordering.domain.service.PayoutService;

import ordering.domain.service.PayslipPdfService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;



@RestController
@RequestMapping("/financier/payouts")
@RequiredArgsConstructor
public class PayoutController {

    private final PayoutService payoutService;
    private final PayslipPdfService payslipPdfService;

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<PayoutSummaryDTO>> getPayoutHistory(@PathVariable String restaurantId) {
        List<Payout> payouts = payoutService.getPayoutHistoryForRestaurant(restaurantId);
        List<PayoutSummaryDTO> payoutDtos = payouts.stream()
                .map(PayoutMapperDto::toDto)
                .toList();

        return ResponseEntity.ok(payoutDtos);
    }


    @GetMapping("/summary")
    public List<PayoutSummaryDTO> getPayoutSummaries() {
        return payoutService.getAllRestaurantPayoutSummaries();
    }

    @PatchMapping("/{payoutId}/mark-paid")
    public ResponseEntity<Void> markPayoutAsPaid(@PathVariable String payoutId) {
        payoutService.markAsPaid(payoutId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{restaurantId}/payslip")
    public ResponseEntity<RestaurantPayslipDTO> getRestaurantPayslip(@PathVariable String restaurantId) {
        return ResponseEntity.ok(payoutService.getPayslipForRestaurant(restaurantId));
    }
    @GetMapping("/{restaurantId}/payslip/pdf")
    public ResponseEntity<byte[]> downloadPayslipPdf(@PathVariable String restaurantId) {
        RestaurantPayslipDTO payslip = payoutService.getPayslipForRestaurant(restaurantId);
        try {
            byte[] pdfBytes = payslipPdfService.generatePayslipPdf(payslip);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=payslip_" + restaurantId + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}
