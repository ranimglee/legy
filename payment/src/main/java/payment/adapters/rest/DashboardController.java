package payment.adapters.rest;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import payment.application.service.DashboardService;
import payment.application.service.InvestmentExportService;
import payment.application.dto.Out.DashboardSummaryResponse;
import payment.application.dto.Out.RoiChartPoint;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/financier/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final InvestmentExportService investmentExportService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        DashboardSummaryResponse summary = dashboardService.computeDashboardSummary(pageable);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/chart-data/roi-over-time")
    public ResponseEntity<List<RoiChartPoint>> getRoiChart(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        List<RoiChartPoint> chartData = dashboardService.getRoiChartOverTime(from, to, pageable);
        return ResponseEntity.ok(chartData);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<Resource> exportToPdf(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) throws FileNotFoundException, JRException {
        Pageable pageable = PageRequest.of(page, size);
        byte[] data = investmentExportService.exportInvestmentsToPdf(pageable);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=investments.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new ByteArrayResource(data));
    }

    @GetMapping("/export/csv")
    public ResponseEntity<Resource> exportToCsv(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) throws FileNotFoundException, JRException {
        Pageable pageable = PageRequest.of(page, size);
        byte[] data = investmentExportService.exportInvestmentsToCsv(pageable);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=investments.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new ByteArrayResource(data));
    }

    @GetMapping("/export/excel")
    public ResponseEntity<Resource> exportToExcel(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) throws FileNotFoundException, JRException {
        Pageable pageable = PageRequest.of(page, size);
        byte[] data = investmentExportService.exportInvestmentsToExcel(pageable);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=investments.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new ByteArrayResource(data));
    }
}