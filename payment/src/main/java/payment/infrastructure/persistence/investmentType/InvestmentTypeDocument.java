package payment.infrastructure.persistence.investmentType;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import payment.domain.model.FieldDefinition;
import payment.domain.model.InvestmentTypeDefinition;

import java.util.Map;

@Document(collection = "investment_type_definitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentTypeDocument {

    @Id
    private String id;

    private String name;

    private Map<String, FieldDefinition> fields;

    private String processorClassName;
    private Map<String, String> derivedFieldFormulas;

    public InvestmentTypeDefinition toDomain() {
        return InvestmentTypeDefinition.builder()
                .id(this.id)
                .name(this.name)
                .fields(this.fields)
                .derivedFieldFormulas(this.derivedFieldFormulas)
                .processorClassName(this.processorClassName)
                .build();
    }

    public static InvestmentTypeDocument fromDomain(InvestmentTypeDefinition definition) {
        return InvestmentTypeDocument.builder()
                .id(definition.getId())
                .name(definition.getName())
                .fields(definition.getFields())
                .derivedFieldFormulas(definition.getDerivedFieldFormulas())
                .processorClassName(definition.getProcessorClassName())
                .build();
    }
}
