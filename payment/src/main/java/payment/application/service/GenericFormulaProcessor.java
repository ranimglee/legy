package payment.application.service;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import payment.application.dto.In.InvestmentRequest;
import payment.domain.model.FieldDefinition;
import payment.domain.model.InvestmentTypeDefinition;
import payment.domain.service.InvestmentTypeProcessor;

import java.util.HashMap;
import java.util.Map;

@Component("generic")
public class GenericFormulaProcessor implements InvestmentTypeProcessor {

    private final ExpressionParser parser = new SpelExpressionParser();

    @Override
    public void validate(InvestmentRequest request, InvestmentTypeDefinition definition) {
        Map<String, Object> fields = request.getFields();

        for (Map.Entry<String, FieldDefinition> entry : definition.getFields().entrySet()) {
            String fieldName = entry.getKey();
            FieldDefinition fieldDef = entry.getValue();

            if (fieldDef.isRequired() && !fields.containsKey(fieldName)) {
                throw new IllegalArgumentException("Missing required field: " + fieldName);
            }
        }
    }

    @Override
    public Map<String, Object> computeDerivedFields(InvestmentRequest request, InvestmentTypeDefinition definition) {
        Map<String, Object> originalFields = request.getFields();
        Map<String, Object> derived = new HashMap<>();

        // 👇 Sanitize fields: unwrap nested maps if needed
        Map<String, Object> sanitizedFields = new HashMap<>();
        for (Map.Entry<String, Object> entry : originalFields.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map map && map.containsKey("value")) {
                sanitizedFields.put(entry.getKey(), map.get("value"));
            } else {
                sanitizedFields.put(entry.getKey(), value);
            }
        }

        // Set up SpEL context with sanitized values
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariables(sanitizedFields);
        context.setVariable("amount", request.getAmount());

        Map<String, String> formulas = definition.getDerivedFieldFormulas();
        if (formulas == null || formulas.isEmpty()) {
            return derived;
        }

        for (Map.Entry<String, String> entry : formulas.entrySet()) {
            String fieldKey = entry.getKey();
            String expression = entry.getValue();

            try {
                Object result = parser.parseExpression(expression).getValue(context);
                derived.put(fieldKey, result);
            } catch (Exception e) {
                throw new RuntimeException("Failed to evaluate formula for field '" + fieldKey + "': " + expression, e);
            }
        }

        return derived;
    }

}
