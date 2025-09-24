package payment.application.dto.Out;

import lombok.Builder;
import lombok.Getter;
import payment.domain.model.enums.InvestmentType;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class InvestmentResponse {
    private Double amount;
    private String description;
    private String investorName;
    private String email;
    private LocalDateTime date;
    private String typeId;
    private String ref;
    private Map<String, Object> fields;
    private Map<String, Object> derivedFields;
}
