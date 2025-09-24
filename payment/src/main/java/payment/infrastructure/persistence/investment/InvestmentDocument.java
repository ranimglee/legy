package payment.infrastructure.persistence.investment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import payment.domain.model.Investment;
import payment.domain.model.InvestmentTypeDefinition;

import java.lang.Double;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "investments")

public class InvestmentDocument {
    @Id
    private String id;
    private String ref;
    private String investorName;
    private Double amount;
    private String description;
    private LocalDateTime date;
    private String email;

    private String typeId;
    private Map<String, Object> fieldValues;
    private Map<String, Object> derivedFields;

    public static InvestmentDocument fromDomain(Investment investment) {
        InvestmentDocument doc = new InvestmentDocument();
        doc.setId(investment.getId());
        doc.setRef(investment.getRef());
        doc.setInvestorName(investment.getInvestorName());
        doc.setAmount(investment.getAmount());
        doc.setDescription(investment.getDescription());
        doc.setDate(investment.getDate());
        doc.setEmail(investment.getEmail());


        doc.setTypeId(investment.getTypeId());
        doc.setFieldValues(investment.getFieldValues());
        doc.setDerivedFields(investment.getDerivedFields());

        return doc;
    }


    public Investment toDomain() {
        Investment investment = new Investment();
        investment.setId(this.id);
        investment.setRef(this.ref);
        investment.setInvestorName(this.investorName);
        investment.setAmount(this.amount);
        investment.setDescription(this.description);
        investment.setDate(this.date);
        investment.setEmail(this.email);


        investment.setTypeId(this.typeId);
        investment.setFieldValues(this.fieldValues);
        investment.setDerivedFields(this.derivedFields);

        return investment;
    }
}





