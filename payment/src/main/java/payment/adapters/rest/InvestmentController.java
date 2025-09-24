package payment.adapters.rest;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import payment.application.dto.In.InvestmentRequest;
import payment.application.dto.Out.InvestmentResponse;
import payment.application.service.InvestmentService;
import payment.domain.model.Investment;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/financier/investments")
@RequiredArgsConstructor
public class InvestmentController {

    private final InvestmentService investmentService;

    @PostMapping("/add-investment")
    public ResponseEntity<InvestmentResponse> createInvestment(@Valid @RequestBody InvestmentRequest request) throws MessagingException {
        Investment saved = investmentService.createInvestment(request);
        return ResponseEntity.ok(investmentService.toResponse(saved));
    }


    @PutMapping("/update-investment/{id}")
    public ResponseEntity<InvestmentResponse> updateInvestment(@PathVariable String id, @Valid @RequestBody Investment investment) {
        investment.setId(id);
        Investment updated = investmentService.updateInvestment(investment);
        return ResponseEntity.ok(investmentService.toResponse(updated));
    }


    // Delete an investment by ID
    @DeleteMapping("/delete-investment/{id}")
    public ResponseEntity<Void> deleteInvestment(@PathVariable String id) {
        investmentService.deleteInvestment(id);
        return ResponseEntity.noContent().build();
    }

    // Retrieve paginated investments
    @GetMapping("/get-all-investments")
    public ResponseEntity<Page<Investment>> getAllInvestments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Investment> investments = investmentService.getAllInvestments(pageable);
        return ResponseEntity.ok(investments);
    }

    @GetMapping("/get-investment-by-id/{id}")
    public ResponseEntity<Investment> getInvestmentById(@PathVariable String id) {
        Investment investment = investmentService.findById(id);
        return ResponseEntity.ok(investment);
    }


}
