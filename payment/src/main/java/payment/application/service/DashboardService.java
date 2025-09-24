package payment.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import payment.application.dto.Out.DashboardSummaryResponse;
import payment.application.dto.Out.RoiChartPoint;
import payment.domain.model.Investment;
import payment.domain.repository.InvestmentRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final InvestmentRepository investmentRepository;

    public DashboardSummaryResponse computeDashboardSummary(Pageable pageable) {
        List<Investment> all = investmentRepository.findAll(pageable).getContent();

        double totalInvested = all.stream()
                .mapToDouble(inv -> Optional.ofNullable(inv.getAmount()).orElse(0.0))
                .sum();

        double totalROI = all.stream()
                .mapToDouble(inv -> Optional.ofNullable(inv.getROI()).orElse(0.0))
                .sum();

        long totalInvestments = all.size();

        String topInvestor = all.stream()
                .filter(i -> i.getInvestorName() != null)
                .collect(Collectors.groupingBy(Investment::getInvestorName, Collectors.summingDouble(i -> Optional.ofNullable(i.getAmount()).orElse(0.0))))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        return DashboardSummaryResponse.builder()
                .totalInvested(totalInvested)
                .totalROI(totalROI)
                .totalInvestments(totalInvestments)
                .topInvestor(topInvestor)
                .build();
    }

    public List<RoiChartPoint> getRoiChartOverTime(LocalDate from, LocalDate to,Pageable pageable) {
        List<Investment> all = investmentRepository.findAll(pageable).getContent();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        return all.stream()
                .filter(inv -> inv.getDate() != null &&
                        !inv.getDate().toLocalDate().isBefore(from) &&
                        !inv.getDate().toLocalDate().isAfter(to))
                .collect(Collectors.groupingBy(
                        inv -> inv.getDate().format(formatter),
                        Collectors.summingDouble(inv -> Optional.ofNullable(inv.getROI()).orElse(0.0))
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new RoiChartPoint(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
