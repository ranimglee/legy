package payment.domain.model;


import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class InvestmentTypeDefinition {
    private String id;
    private String name;
    private Map<String, FieldDefinition> fields;
    private String processorClassName;
   private Map<String, String> derivedFieldFormulas;
}