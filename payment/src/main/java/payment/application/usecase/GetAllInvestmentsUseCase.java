package payment.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import payment.application.dto.Out.InvestmentResponse;
import payment.domain.model.Investment;
import payment.application.service.InvestmentService;

@Service
@RequiredArgsConstructor
public class GetAllInvestmentsUseCase {

    private final InvestmentService investmentService;

    public Page<InvestmentResponse> execute(Pageable pageable) {
        return investmentService.getAllInvestments(pageable)
                .map(this::mapToResponse);
    }

    private InvestmentResponse mapToResponse(Investment investment) {
        return InvestmentResponse.builder()
                .amount(investment.getAmount())
                .investorName(investment.getInvestorName())
                .description(investment.getDescription())
                .date(investment.getDate())
                .typeId(investment.getTypeId())
                .build();
    }
}
