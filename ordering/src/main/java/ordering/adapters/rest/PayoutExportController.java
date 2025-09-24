package ordering.adapters.rest;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import ordering.application.dto.order.WeeklyCourierPaymentSummary;
import ordering.application.dto.payout.CourierPayoutResponseDto;
import ordering.domain.service.WeeklyCourierPayoutService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import shared.domain.service.CourierQueryService;
import shared.dto.CourierProfileDTO;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
@AllArgsConstructor
@RestController
@RequestMapping("/financier/driver-payout")

public class PayoutExportController {
    private final CourierQueryService courierQueryService;
    private final WeeklyCourierPayoutService payoutService;

    @GetMapping("/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=weekly-payouts.pdf");

        // Use try-with-resources for Document
        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font fontTitle = new Font(Font.HELVETICA, 18, Font.BOLD);
            document.add(new Paragraph("Weekly Courier Payout Summary", fontTitle));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(10);
            table.setWidthPercentage(100);

            String[] headers = {"Courier ID", "Name", "Delivered", "Refused", "Delivery Payout", "Bonus", "Penalty", "Final Payout", "Orders", "Avg/Delivery"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell();
                cell.setPhrase(new Paragraph(header));
                table.addCell(cell);
            }

            LocalDate now = LocalDate.now();
            Date start = Date.from(now.minusWeeks(1).with(java.time.DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date end = Date.from(now.minusWeeks(1).with(java.time.DayOfWeek.SUNDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());

            List<CourierProfileDTO> couriers = courierQueryService.getAllCouriers();
            List<WeeklyCourierPaymentSummary> summaries = couriers.stream()
                    .map(courier -> payoutService.calculateWeeklyPayout(courier, start, end))
                    .toList();

            for (WeeklyCourierPaymentSummary summary : summaries) {
                table.addCell(summary.deliveryPersonId());
                table.addCell(summary.deliveryPersonName());
                table.addCell(String.valueOf(summary.totalDeliveries()));
                table.addCell(String.valueOf(summary.totalRefusals()));
                table.addCell(String.valueOf(summary.totalDeliveryPayout()));
                table.addCell(String.valueOf(summary.bonus()));
                table.addCell(String.valueOf(summary.penalty()));
                table.addCell(String.valueOf(summary.finalPayout()));
                table.addCell(String.valueOf(summary.totalOrders()));
                table.addCell(String.valueOf(summary.avgPayoutPerDelivery()));
            }

            document.add(table);

        }
    }

    @GetMapping("/weekly")
    public List<CourierPayoutResponseDto> getWeeklyCourierPayouts(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
            ) {
        Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());


        List<CourierProfileDTO> couriers = courierQueryService.getAllCouriers();

        return couriers.stream()
                .map(courier -> {
                    WeeklyCourierPaymentSummary summary = payoutService.calculateWeeklyPayout(courier, start, end);
                    return new CourierPayoutResponseDto(
                            courier.id(),
                            courier.firstname(),
                            courier.lastname(),
                            courier.email(),
                            courier.phoneNumber(),
                            summary.totalDeliveries(),
                            summary.totalRefusals(),
                            summary.totalDeliveryPayout(),
                            summary.bonus(),
                            summary.penalty(),
                            summary.finalPayout(),
                            summary.totalOrders(),
                            summary.avgPayoutPerDelivery()
                    );
                })
                .toList();
    }

    /**
     * Marks the weekly payout as paid.
     */
    @PostMapping("/mark-paid")
    public WeeklyCourierPaymentSummary markWeeklyPayoutAsPaid(
            @RequestBody WeeklyCourierPaymentSummary summary
    ) {
        return payoutService.markWeeklyPayoutAsPaid(summary);
    }
}
