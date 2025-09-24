package payment.domain.service;

import payment.application.dto.In.InvestmentRequest;
import payment.domain.model.InvestmentTypeDefinition;

import java.util.Map;

public interface InvestmentTypeProcessor {
     void validate(InvestmentRequest request, InvestmentTypeDefinition definition);

     Map<String, Object> computeDerivedFields(InvestmentRequest request, InvestmentTypeDefinition definition);
}
